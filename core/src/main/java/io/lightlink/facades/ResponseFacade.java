package io.lightlink.facades;

/*
 * #%L
 * lightlink-core
 * %%
 * Copyright (C) 2015 Vitaliy Shevchuk
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */


import io.lightlink.core.RunnerContext;
import io.lightlink.output.ResponseStream;

import java.io.IOException;


public class ResponseFacade {

    RunnerContext runnerContext;

    public ResponseFacade(RunnerContext runnerContext) {
        this.runnerContext = runnerContext;
    }

    public void writeObject(String property, Object data) throws IOException {
        runnerContext.getResponseStream().writeProperty(property, data);
    }

    public void setFormat(ResponseStream responseStream){
        runnerContext.setResponseStream(responseStream);
    }

    public ResponseStream getFormat(){
        return runnerContext.getResponseStream();
    }



}
