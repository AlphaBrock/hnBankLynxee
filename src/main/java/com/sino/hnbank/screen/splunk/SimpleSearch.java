/*
 * Copyright 2011 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.sino.hnbank.screen.splunk;

import com.splunk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// Note: not all search parameters are exposed to the CLI for this example.
public class SimpleSearch {
    private static final Logger logger = LoggerFactory.getLogger(SimpleSearch.class);
    private SimpleSearch(){

    }

    private static class SingletonHolder{
        private final static SimpleSearch instance=new SimpleSearch();
    }

    public static SimpleSearch getInstance(){
        return SingletonHolder.instance;
    }

    public InputStream search(String[] args) {
        InputStream stream = null;
        try {
            stream = run(args);
        }
        catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
        return stream;
    }

    InputStream run(String[] args) throws IOException {
        Command command = Command.splunk("search");
        command.parse(args);

        if (command.args.length != 1)
            Command.error("Search expression required");
        String query = command.args[0];
        HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);

        Service service = Service.connect(command.opts);

        // Check the syntax of the query.
        try {
            Args parseArgs = new Args("parse_only", true);
            service.parse(query, parseArgs);
        }
        catch (HttpException e) {
            String detail = e.getDetail();
            logger.error(e.toString());
            return null;
        }

        // This is the simplest form of searching splunk. Note that additional
        // arguments are allowed, but they are not shown in this example.
        Args outputArgs = new Args("output_mode", JobEventsArgs.OutputMode.XML);
        InputStream stream = service.oneshotSearch(query, outputArgs);

        return stream;
    }
}
