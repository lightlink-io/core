package io.lightlink.output;

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


import io.lightlink.core.Hints;
import io.lightlink.core.RunnerContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public interface ResponseStream {

    void end();

    void writeProperty(String name, Object value);

    void writeFullObjectToArray(Object value);

    void writePropertyObjectStart(String name);

    void writePropertyObjectEnd();

    void writePropertyArrayStart(String name);

    void writePropertyArrayEnd();

    void writeObjectStart();

    void writeObjectEnd();

    public void setRunnerContext(RunnerContext runnerContext) ;

    boolean checkConnectionAlive();

    public void setContentType(String value);
    public void setHeader(String header, String value);
    public void flushBuffer()  throws IOException;
}
