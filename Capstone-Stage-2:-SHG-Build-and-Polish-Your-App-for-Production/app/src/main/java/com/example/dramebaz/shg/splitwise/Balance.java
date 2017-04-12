package com.example.dramebaz.shg.splitwise;

import org.json.JSONException;
import org.json.JSONObject;

public class Balance {
    public String amount;
    public String currencyCode;

    public static Balance fromJSONObject(JSONObject jsonObject) throws JSONException {
        Balance b = new Balance();
        b.amount = jsonObject.getString("amount");
        b.currencyCode = jsonObject.getString("currency_code");
        return b;
    }
}
