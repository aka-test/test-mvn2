/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.components.echocommon;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=OptionProcessor.class)
public class EchoOption extends OptionProcessor {

    public static final String PARAM_ECHO = "echo_conway_admin";
    private static Option echoEnv = Option.withoutArgument(Option.NO_SHORT_NAME, PARAM_ECHO);

    @Override
    protected Set<Option> getOptions() {
        // Ticket 232 - Always have the login screen.
        LoginForm lf = new LoginForm(null, "Enter Echo Admin password", 
                "Enter Echo admin password and click on OK to enable Echo admin tools OR click Cancel to continue without Echo admin tools.",
                "admin", "");
        lf.setVisible(true);
        if (lf.isLoggedIn()) {
            EchoUtil.addParam(PARAM_ECHO, "");
        }

        return Collections.singleton(echoEnv);
    }

    @Override
    protected void process(Env env, Map<Option, String[]> maps) throws CommandException {
        // Ticket 232 - Always have the login screen.
    }

}
