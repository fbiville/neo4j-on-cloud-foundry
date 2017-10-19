package org.neo4j.commandline.admin.security;

import org.neo4j.commandline.admin.AdminCommand;
import org.neo4j.commandline.admin.AdminCommandSection;
import org.neo4j.commandline.admin.OutsideWorld;
import org.neo4j.commandline.arguments.Arguments;

import java.nio.file.Path;

import static org.neo4j.kernel.api.security.UserManager.INITIAL_USER_NAME;

public class SetPasswordCommandProvider extends AdminCommand.Provider
{

    public SetPasswordCommandProvider()
    {
        super( "set-password" );
    }


    @Override
    public Arguments allArguments()
    {
        return new Arguments()
                .withMandatoryPositionalArgument(0, "password");
    }

    @Override
    public String summary()
    {
        return "Sets the password of the admin user ('" + INITIAL_USER_NAME + "').";
    }

    @Override
    public AdminCommandSection commandSection()
    {
        return AuthenticationCommandSection.instance();
    }

    @Override
    public String description()
    {
        return "Sets the initial (admin) user.";
    }

    @Override
    public AdminCommand create(Path homeDir, Path configDir, OutsideWorld outsideWorld )
    {
        return new SetPasswordCommand( homeDir, configDir, outsideWorld );
    }
}
