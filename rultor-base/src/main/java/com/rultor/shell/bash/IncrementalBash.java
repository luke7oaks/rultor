/**
 * Copyright (c) 2009-2013, rultor.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the rultor.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.rultor.shell.bash;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.immutable.Array;
import com.jcabi.log.Logger;
import com.rultor.shell.Batch;
import com.rultor.shell.Shells;
import com.rultor.tools.Vext;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Incremental bash batch.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
@EqualsAndHashCode(of = { "shells", "commands" })
@Loggable(Loggable.DEBUG)
public final class IncrementalBash implements Batch {

    /**
     * Shells.
     */
    private final transient Shells shells;

    /**
     * Commands to execute.
     */
    private final transient Array<Vext> commands;

    /**
     * Public ctor.
     * @param shls Shells
     * @param cmds Commands
     */
    public IncrementalBash(
        @NotNull(message = "shells can't be NULL") final Shells shls,
        @NotNull(message = "list of commands can't be NULL")
        final Collection<String> cmds) {
        this.shells = shls;
        final Collection<Vext> vexts = new ArrayList<Vext>(cmds.size());
        for (String cmd : cmds) {
            vexts.add(new Vext(cmd));
        }
        this.commands = new Array<Vext>(vexts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Loggable(value = Loggable.DEBUG, limit = Integer.MAX_VALUE)
    public int exec(
        @NotNull(message = "args can't be NULL") final Map<String, Object> args,
        @NotNull(message = "stream can't be NULL") final OutputStream output)
        throws IOException {
        return new Bash(this.shells, this.script()).exec(args, output);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Logger.format(
            "bash batch of %d command(s) through %s",
            this.commands.size(), this.shells
        );
    }

    /**
     * Make a script.
     * @return Bash script
     */
    private String script() {
        return "";
    }

}
