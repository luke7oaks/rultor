/**
 * Copyright (c) 2009-2014, rultor.com
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
package com.rultor.spi;

import com.jcabi.aspects.Immutable;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.w3c.dom.Node;
import org.xembly.Directive;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Talk.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
public interface Talk {

    /**
     * Its unique name.
     * @return Its name
     * @throws IOException If fails
     */
    String name() throws IOException;

    /**
     * Read its content.
     * @return Content
     * @throws IOException If fails
     */
    XML read() throws IOException;

    /**
     * Modify its content.
     * @param dirs Directives
     * @throws IOException If fails
     */
    void modify(Iterable<Directive> dirs) throws IOException;

    /**
     * Make it active or passive.
     * @param yes TRUE if it should be active
     * @throws IOException If fails
     */
    void active(boolean yes) throws IOException;

    /**
     * In file.
     */
    @Immutable
    final class InFile implements Talk {
        /**
         * File.
         */
        private final transient String path;
        /**
         * Ctor.
         * @throws IOException If fails
         */
        public InFile() throws IOException {
            this(File.createTempFile("rultor", ".talk"));
            FileUtils.write(
                new File(this.path),
                String.format("<talk name='%s'/>", this.name())
            );
        }
        /**
         * Ctor.
         * @param file The file
         */
        public InFile(final File file) {
            this.path = file.getAbsolutePath();
        }
        @Override
        public String name() {
            return "a1b2c3d4";
        }
        @Override
        public XML read() throws IOException {
            return new XMLDocument(
                FileUtils.readFileToString(
                    new File(this.path), CharEncoding.UTF_8
                )
            );
        }
        @Override
        public void modify(final Iterable<Directive> dirs) throws IOException {
            final Node node = this.read().node();
            try {
                new Xembler(dirs).apply(node);
            } catch (final ImpossibleModificationException ex) {
                throw new IllegalStateException(ex);
            }
            FileUtils.write(
                new File(this.path),
                new XMLDocument(node).toString(),
                CharEncoding.UTF_8
            );
        }
        @Override
        public void active(final boolean yes) {
            // nothing
        }
    }

}