/*
 * Copyright (c) 2002-2016 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.commandline.admin.security;

import org.neo4j.commandline.admin.AdminCommand;
import org.neo4j.commandline.admin.CommandFailed;
import org.neo4j.commandline.admin.IncorrectUsage;
import org.neo4j.commandline.admin.OutsideWorld;
import org.neo4j.helpers.Args;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.security.Credential;
import org.neo4j.kernel.impl.security.User;
import org.neo4j.logging.NullLogProvider;
import org.neo4j.server.configuration.ConfigLoader;
import org.neo4j.server.security.auth.CommunitySecurityModule;
import org.neo4j.server.security.auth.FileUserRepository;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.neo4j.kernel.api.security.UserManager.INITIAL_USER_NAME;

public class SetPasswordCommand implements AdminCommand {

    private final Path homeDir;
    private final Path configDir;
    private final OutsideWorld outsideWorld;

    public SetPasswordCommand(Path homeDir, Path configDir, OutsideWorld outsideWorld) {
        this.homeDir = homeDir;
        this.configDir = configDir;
        this.outsideWorld = outsideWorld;
    }

    @Override
    public void execute(String[] args) throws IncorrectUsage, CommandFailed {
        try {
            setPassword(parsePassword(args));
        } catch (Exception e) {
            throw new CommandFailed("Failed run 'set-password' on '" + INITIAL_USER_NAME + "': " + e.getMessage(), e);
        } catch (Throwable t) {
            throw new CommandFailed("Failed run 'set-password' on '" + INITIAL_USER_NAME + "': " + t.getMessage(), new RuntimeException(t.getMessage()));
        }
    }

    private String parsePassword(String[] args) throws IncorrectUsage {
        List<String> orphans = Args.parse(args).orphans();
        int orphanCount = orphans.size();
        if (orphans.isEmpty() || orphanCount > 1) {
            throw new IncorrectUsage(String.format("Missing argument: 'set-password' expects a single argument (password), %d given", orphanCount));
        }
        return orphans.get(0);
    }

    private void setPassword(String password) throws Throwable {
        File file = CommunitySecurityModule.getUserRepositoryFile(loadNeo4jConfig());

        FileUserRepository userRepository = new FileUserRepository(outsideWorld.fileSystem(), file, NullLogProvider.getInstance());
        userRepository.start();
        upsert(password, userRepository);
        userRepository.shutdown();
        outsideWorld.stdOutLine("Changed password for user '" + INITIAL_USER_NAME + "'");
    }

    private void upsert(String password, FileUserRepository userRepository) throws Exception {
        User existingUser = userRepository.getUserByName(INITIAL_USER_NAME);
        User newUser = new User.Builder(INITIAL_USER_NAME, Credential.forPassword(password))
                .withRequiredPasswordChange(false)
                .build();

        if (existingUser == null) {
            userRepository.create(newUser);
        } else {
            userRepository.update(existingUser, newUser);
        }
    }

    private Config loadNeo4jConfig() {
        return ConfigLoader.loadConfig(
                Optional.of(homeDir.toFile()),
                Optional.of(configDir.resolve("neo4j.conf").toFile()));
    }

}
