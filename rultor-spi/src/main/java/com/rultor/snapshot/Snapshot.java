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
package com.rultor.snapshot;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.w3c.dom.Document;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;
import org.xembly.XemblySyntaxException;

/**
 * Snapshot.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
@ToString
@EqualsAndHashCode(of = "xmbl")
@Loggable(Loggable.DEBUG)
public final class Snapshot {

    /**
     * Script.
     */
    private final transient String xmbl;

    /**
     * Public ctor.
     * @param stream Stream to read it from
     * @throws IOException If fails to read
     */
    public Snapshot(final InputStream stream) throws IOException {
        this(Snapshot.fetch(stream));
    }

    /**
     * Public ctor.
     * @param script Script
     */
    public Snapshot(final String script) {
        this.xmbl = script;
    }

    /**
     * Get xembly script.
     * @return The script
     */
    public String xembly() {
        return this.xmbl;
    }

    /**
     * Convert it to DOM document.
     * @return DOM document
     */
    public Document dom() {
        final Document dom;
        try {
            dom = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        }
        dom.appendChild(dom.createElement("snapshot"));
        try {
            new Xembler(new Directives(this.xmbl)).exec(dom);
        } catch (XemblySyntaxException ex) {
            throw new IllegalArgumentException(ex);
        } catch (ImpossibleModificationException ex) {
            throw new IllegalArgumentException(ex);
        }
        return dom;
    }

    /**
     * Fetch script from the stream.
     * @param stream Input stream where to find details
     * @return The script
     * @throws IOException If IO problem inside
     */
    private static String fetch(final InputStream stream) throws IOException {
        final BufferedReader reader = new BufferedReader(
            new InputStreamReader(stream)
        );
        final StringBuilder buf = new StringBuilder();
        while (true) {
            final String line = reader.readLine();
            if (line == null) {
                break;
            }
            if (XemblyLine.existsIn(line)) {
                buf.append(XemblyLine.parse(line).xembly());
            }
        }
        return buf.toString();
    }

}
