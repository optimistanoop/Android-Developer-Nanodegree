package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import android.util.Pair;

/**
 * Created by dramebaz on 24/7/16.
 */
public class GCETest  extends AndroidTestCase {

               public void runGCETest() {
                String joke = null;
                EndpointAsyncTask jokesAsyncTask = new EndpointAsyncTask();
                jokesAsyncTask.execute(new Pair<Context, String>(getContext(), ""));
                try {
                        joke = jokesAsyncTask.get();
                        Log.d("GCETest", "got non empty joke : " + joke);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                assertNotNull(joke);
            }
    }
