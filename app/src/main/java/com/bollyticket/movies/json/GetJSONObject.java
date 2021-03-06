/*
 * Copyright (C) 2016 Bolly Ticket
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bollyticket.movies.json;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;

public class GetJSONObject {

    public static JSONObject getJSONObject(String url) throws IOException,
            JSONException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            jsonObject = jsonParser.getJSONHttpURLConnection(url);
        } else {
            jsonObject = jsonParser.getJSONHttpClient(url);
        }
        return jsonObject;
    }
}
