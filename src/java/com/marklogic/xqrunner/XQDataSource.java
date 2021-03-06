/*
 * Copyright (c)2004 Mark Logic Corporation
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
 *
 * The use of the Apache License does not indicate that this project is
 * affiliated with the Apache Software Foundation.
 */
package com.marklogic.xqrunner;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Aug 25, 2004
 * Time: 5:58:30 PM
 */
public interface XQDataSource
{
	XQRunner newSyncRunner();
	XQAsyncRunner newAsyncRunner();
	XQAsyncRunner newAsyncRunner (XQRunner runner);

	XQuery newQuery (String body);

	XQVariable newVariable (String namespace, String localname, XQVariableType type, Object value);
	XQVariable newVariable (String localname, XQVariableType type, Object value);
	XQVariable newVariable (String namespace, String localname, XQVariableType type, long value);
	XQVariable newVariable (String localname, XQVariableType type, long value);
	XQVariable newVariable (String namespace, String localname, XQVariableType type, double value);
	XQVariable newVariable (String localname, XQVariableType type, double value);
	XQVariable newVariable (String namespace, String localname, boolean value);
	XQVariable newVariable (String localname, boolean value);

	XQDuration newDuration (int years, int months, int days,
		int hours, int minutes, int seconds, double subseconds);
	XQDuration newDuration (boolean positive, int years, int months, int days,
		int hours, int minutes, int seconds, double subseconds);
	XQDuration newDuration (String value);
}
