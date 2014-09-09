/**
 * Copyright (c) 2012, Oliver Kleine, Institute of Telematics, University of Luebeck
 * All rights reserved
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *  - Redistributions of source messageCode must retain the above copyright notice, this list of conditions and the following
 *    disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *  - Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.uniluebeck.itm.ncoap.communication.observe.client;

import de.uniluebeck.itm.ncoap.application.client.Token;

import java.net.InetSocketAddress;

/**
 * Instances of this class are sent downstream if a client decides to stop a running observation.
 *
 * @author Oliver Kleine
 */
public class InternalStopObservationMessage {

    private InetSocketAddress remoteEndpoint;
    private Token token;

    /**
     * Creates a new instance of
     * {@link de.uniluebeck.itm.ncoap.communication.observe.client.InternalStopObservationMessage}
     *
     * @param remoteEndpoint the {@link java.net.InetSocketAddress} of the remote endpoint providing the observed
     *                       service
     * @param token the {@link de.uniluebeck.itm.ncoap.application.client.Token} to identify the observation to be
     *              stopped
     */
    public InternalStopObservationMessage(InetSocketAddress remoteEndpoint, Token token) {
        this.remoteEndpoint = remoteEndpoint;
        this.token = token;
    }


    /**
     * Returns the {@link java.net.InetSocketAddress} of the remote endpoint providing the observed service
     * @return the {@link java.net.InetSocketAddress} of the remote endpoint providing the observed service
     */
    public InetSocketAddress getRemoteEndpoint() {
        return remoteEndpoint;
    }


    /**
     * Returns the {@link de.uniluebeck.itm.ncoap.application.client.Token} to identify the observation to be stopped
     * @return the {@link de.uniluebeck.itm.ncoap.application.client.Token} to identify the observation to be stopped
     */
    public Token getToken() {
        return token;
    }
}
