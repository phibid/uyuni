/*
 * Copyright (c) 2009--2014 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package com.redhat.rhn.frontend.xmlrpc;

import com.redhat.rhn.common.hibernate.LookupException;
import com.redhat.rhn.common.translation.Translator;
import com.redhat.rhn.domain.user.User;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import redstone.xmlrpc.XmlRpcInvocation;
import redstone.xmlrpc.XmlRpcInvocationInterceptor;


/**
 * LoggingInvocationProcessor extends the marquee-xmlrpc library to allow
 * us to log method calls.
 */
public class LoggingInvocationProcessor implements XmlRpcInvocationInterceptor {
    private static Logger log = LogManager.getLogger(LoggingInvocationProcessor.class);
    private static ThreadLocal<User> caller = new ThreadLocal<>();

    private static ThreadLocal timer = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new StopWatch();
        }
    };

    /**
     * {@inheritDoc}
     */
    public boolean before(XmlRpcInvocation invocation) {

        // we start the timing and return true so processing
        // continues.
        // NOTE: as of commons-lang 2.1 we must reset before
        // starting.
        getStopWatch().reset();
        getStopWatch().start();

        List arguments = invocation.getArguments();
        // HACK ALERT!  We need the caller, would be better in
        // the postProcess, but that works for ALL methods except
        // logout.  So we do it here.
        if ((arguments != null) && (arguments.size() > 0)) {
            if (arguments.get(0) instanceof User) {
                setCaller((User)arguments.get(0));
            }
            else {
                String arg = (String) Translator.convert(
                        arguments.get(0), String.class);
                if (potentialSessionKey(arg)) {
                    setCaller(getLoggedInUser(arg));
                }
                else {
                    caller.remove();
                }
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Object after(XmlRpcInvocation invocation, Object returnValue) {
        StringBuffer buf = new StringBuffer();
        try {
            // Create the call in a separate buffer for reuse

            buf.append("REQUESTED FROM: ");
            buf.append(RhnXmlRpcServer.getCallerIp());
            buf.append(" CALL: ");
            String call = invocation.getHandlerName() + "." + invocation.getMethodName() + "(" +
                    processArguments(invocation.getHandlerName(), invocation.getMethodName(),
                            invocation.getArguments()) + ")";
            buf.append(call);
            buf.append(" CALLER: (");
            buf.append(getCallerLogin());
            buf.append(") TIME: ");

            getStopWatch().stop();

            buf.append(getStopWatch().getTime() / 1000.00);
            buf.append(" seconds");

            log.info(buf);
        }
        catch (RuntimeException e) {
            log.error("postProcess error CALL: {} {}", invocation.getHandlerName(), invocation.getMethodName(), e);
        }

        return returnValue;
    }

    /**
     * {@inheritDoc}
     */
    public void onException(XmlRpcInvocation invocation, Throwable exception) {
        StringBuffer buf = new StringBuffer();
        try {
            buf.append("REQUESTED FROM: ");
            buf.append(RhnXmlRpcServer.getCallerIp());
            buf.append(" CALL: ");
            buf.append(invocation.getHandlerName());
            buf.append(".");
            buf.append(invocation.getMethodName());
            buf.append("(");
            buf.append(processArguments(invocation.getHandlerName(),
                    invocation.getMethodName(), invocation.getArguments()));
            buf.append(") CALLER: (");
            buf.append(getCallerLogin());
            buf.append(") TIME: ");

            getStopWatch().stop();

            buf.append(getStopWatch().getTime() / 1000.00);
            buf.append(" seconds");

            buf.append(System.lineSeparator());
            buf.append(exception);

            log.info(buf);
        }
        catch (RuntimeException e) {
            log.error("postProcess error CALL: {} {}", invocation.getHandlerName(), invocation.getMethodName(), e);
        }
    }

    private StringBuffer processArguments(String handler, String method,
                                  List arguments) {
        StringBuffer ret = new StringBuffer();
        if (arguments != null) {
            int size = arguments.size();
            for (int i = 0; i < size; i++) {
                String arg;
                if (arguments.get(i) instanceof User) {
                    arg = ((User)arguments.get(i)).getLogin();
                }
                else {
                    if (preventValueLogging(handler, method, i)) {
                        arg = "******";
                    }
                    else {
                        arg = (String) Translator.convert(arguments.get(i), String.class);
                    }
                }

                ret.append(arg);

                if ((i + 1) < size) {
                    ret.append(", ");
                }
            }
        }
        return ret;
    }

    // determines whether the value should be hidden from logging
    private static boolean preventValueLogging(String handler, String method, int argPosition) {
        String handlerAndMethod = handler + "." + method;
        switch (handlerAndMethod) {
            case "auth.login":
                return argPosition == 1;
            case "system.bootstrap":
                return argPosition == 4;
            case "system.bootstrapWithPrivateKey":
                return argPosition == 4 || argPosition == 5;
            case "admin.payg.create":
                return argPosition == 5 || argPosition == 6 || argPosition == 7 ||
                        argPosition == 11 || argPosition == 12 || argPosition == 13;
            case "admin.payg.setDetails":
                return argPosition == 1;
            case "proxy.container_config":
                return argPosition == 8 || argPosition == 6 || argPosition == 7;
            default:
                return false;
        }
    }

    /**
     * If the key is a sessionKey, we'll return the username, otherwise we'll
     * return (unknown).
     * @param key potential sessionKey.
     * @return  username, (Invalid Session ID), or (unknown);
     */
    private User getLoggedInUser(String key) {
        try {
            User user = BaseHandler.getLoggedInUser(key);
            if (user != null) {
                return user;
            }
        }
        catch (LookupException le) {
            // do nothing
        }
        catch (Exception e) {
            log.error("problem with getting logged in user for logging", e);
        }

        return null;
    }

    /**
     * Returns true if the given key contains an 'x' which is the separator
     * character in the session key.
     * @param key Potential key candidate.
     * @return true if the given key contains an 'x' which is the separator
     * character in the session key.
     */
    private boolean potentialSessionKey(String key) {
        if (key == null || key.equals("")) {
            return false;
        }

        // Get the id
        String[] keyParts = StringUtils.split(key, 'x');

        // make sure the id is numeric and can be made into a Long
        return StringUtils.isNumeric(keyParts[0]);
    }

    private static StopWatch getStopWatch() {
        return (StopWatch) timer.get();
    }

    private String getCallerLogin() {
        String ret = "none";
        if (getCaller() != null) {
            ret = getCaller().getLogin();
        }
        return ret;
    }

    private static User getCaller() {
        return (User) caller.get();
    }

    private static void setCaller(User c) {
        caller.set(c);
    }
}
