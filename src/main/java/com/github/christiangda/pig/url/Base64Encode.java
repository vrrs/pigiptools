/*
 * Base64Encode.java
 *
 * Copyright (c) 2015  Christian González
 *
 * This program is free software: you can redistribute it and/or modify
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

package com.github.christiangda.pig.url;

import org.apache.pig.EvalFunc;
import org.apache.pig.FuncSpec;
import org.apache.pig.PigException;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.FrontendException;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.mortbay.jetty.security.B64Code;
import org.mortbay.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encode a string using Base64 algorithm.
 * see https://en.wikipedia.org/wiki/Base64
 * <p>
 * <pre>
 * Example:
 * {@code
 * -- Define function call
 * DEFINE Base64Encode com.github.christiangda.pig.url.Base64Encode();
 *
 * -- input is a TSV of Base64 Encoded strings
 * input = LOAD 'input_file' AS (line:chararray);
 * output = FOREACH input GENERATE Base64Encode(line) AS encoded_string;
 * }
 * </pre>
 */
public class Base64Encode extends EvalFunc<String> {

    @Override
    public String exec(Tuple input) throws IOException {

        // validate input
        if (input == null || input.size() == 0 || input.get(0) == null) {
            return null;
        }

        //
        if (input.get(0) == "") {
            return input.get(0).toString();
        }

        if (input.size() > 1) {
            throw new ExecException("Wrong number of arguments > 1", PigException.ERROR);
        }

        //
        String str;

        //Validating arguments
        Object arg0 = input.get(0);
        if (arg0 instanceof String)
            str = (String) arg0;
        else {
            String msg = "Invalid data type for argument " + DataType.findTypeName(arg0);
            throw new ExecException(msg, PigException.ERROR);
        }

        //decode
        return new String(B64Code.encode(str.getBytes(StringUtil.__UTF8)));
    }

    @Override
    public List<FuncSpec> getArgToFuncMapping() throws FrontendException {
        List<FuncSpec> funcList = new ArrayList<FuncSpec>();
        funcList.add(new FuncSpec(this.getClass().getName(), new Schema(new Schema.FieldSchema(null, DataType.CHARARRAY))));
        return funcList;
    }
}
