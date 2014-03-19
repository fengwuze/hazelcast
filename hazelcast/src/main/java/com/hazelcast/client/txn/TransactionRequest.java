/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.client.txn;

import com.hazelcast.client.CallableClientRequest;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.util.ThreadUtil;

import java.io.IOException;

/**
 * @author ali 21/01/14
 */
public abstract class TransactionRequest extends CallableClientRequest implements Portable {

    int clientThreadId = -1;

    protected TransactionRequest() {
    }

    protected TransactionRequest(int clientThreadId) {
        this.clientThreadId = clientThreadId;
    }

    protected abstract Object innerCall() throws Exception;

    public final Object call() throws Exception {
        ThreadUtil.setThreadId(clientThreadId);
        try {
            return innerCall();
        } finally {
            ThreadUtil.removeThreadId();
        }
    }

    public abstract void write(PortableWriter writer) throws IOException ;

    public abstract void read(PortableReader reader) throws IOException ;

    public final void writePortable(PortableWriter writer) throws IOException {
        writer.writeInt("cti",clientThreadId);
        write(writer);
    }

    public final void readPortable(PortableReader reader) throws IOException {
        clientThreadId = reader.readInt("cti");
        read(reader);
    }
}