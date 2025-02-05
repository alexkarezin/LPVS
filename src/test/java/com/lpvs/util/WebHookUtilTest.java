/**
 * Copyright (c) 2022, Samsung Electronics Co., Ltd. All rights reserved.
 *
 * Use of this source code is governed by a MIT license that can be
 * found in the LICENSE file.
 */

package com.lpvs.util;

import com.lpvs.entity.LPVSQueue;
import com.lpvs.entity.enums.LPVSPullRequestAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WebHookUtilTest {

    @Nested
    class TestGetGitHubWebhookConfig__ForkTrue {
        String json_to_test;
        LPVSQueue expected;

        @BeforeEach
        void setUp() {
            json_to_test =
                "{" +
                    "\"action\": \"opened\", " +
                    "\"repository\": {" +
                        "\"name\": \"LPVS\", " +
                        "\"full_name\": \"Samsung/LPVS\", " +
                        "\"html_url\": \"https://github.com/Samsung/LPVS\"" +
                    "}, " +
                    "\"pull_request\": {" +
                        "\"html_url\": \"https://github.com/Samsung/LPVS/pull/18\", " +
                        "\"head\": {" +
                            "\"repo\": {" +
                                "\"fork\": true, " +
                                "\"html_url\": \"https://github.com/o-kopysov/LPVS/tree/utests\"" +
                            "}, " +
                            "\"sha\": \"edde69ecb8e8a88dde09fa9789e2c9cab7cf7cf9\", " +
                            "\"ref\": \"o-kopysov:utests\"" +
                        "}, " +
                        "\"url\": \"https://api.github.com/repos/Samsung/LPVS/pulls/18\"" +
                    "}" +
                "}";

            expected = new LPVSQueue();
            expected.setAction(LPVSPullRequestAction.OPEN);
            expected.setPullRequestUrl("https://github.com/Samsung/LPVS/pull/18");
            expected.setPullRequestFilesUrl("https://github.com/o-kopysov/LPVS/tree/utests");  // fork == True
            expected.setPullRequestAPIUrl("https://api.github.com/repos/Samsung/LPVS/pulls/18");
            expected.setRepositoryUrl("https://github.com/Samsung/LPVS");
            expected.setUserId("GitHub hook");
            expected.setHeadCommitSHA("edde69ecb8e8a88dde09fa9789e2c9cab7cf7cf9");
            expected.setAttempts(0);
        }

        @Test
        public void testGetGitHubWebhookConfig__ForkTrue() {
            // main test
            assertEquals(expected, LPVSWebhookUtil.getGitHubWebhookConfig(json_to_test));
        }
    }


    @Nested
    class TestGetGitHubWebhookConfig__ForkFalse {
        String json_to_test;
        LPVSQueue expected;

        @BeforeEach
        void setUp() {
            json_to_test =
                "{" +
                    "\"action\": \"opened\", " +
                    "\"repository\": {" +
                        "\"name\": \"LPVS\", " +
                        "\"full_name\": \"Samsung/LPVS\", " +
                        "\"html_url\": \"https://github.com/Samsung/LPVS\"" +
                    "}, " +
                    "\"pull_request\": {" +
                        "\"html_url\": \"https://github.com/Samsung/LPVS/pull/18\", " +
                        "\"head\": {" +
                            "\"repo\": {" +
                                "\"fork\": false, " +
                                "\"html_url\": \"https://github.com/o-kopysov/LPVS/tree/utests\"" +
                            "}, " +
                            "\"sha\": \"edde69ecb8e8a88dde09fa9789e2c9cab7cf7cf9\", " +
                            "\"ref\": \"o-kopysov:utests\"" +
                        "}, " +
                        "\"url\": \"https://api.github.com/repos/Samsung/LPVS/pulls/18\"" +
                    "}" +
                "}";

            expected = new LPVSQueue();
            expected.setAction(LPVSPullRequestAction.OPEN);
            expected.setPullRequestUrl("https://github.com/Samsung/LPVS/pull/18");
            expected.setPullRequestFilesUrl("https://github.com/Samsung/LPVS/pull/18");  // fork == False
            expected.setPullRequestAPIUrl("https://api.github.com/repos/Samsung/LPVS/pulls/18");
            expected.setRepositoryUrl("https://github.com/Samsung/LPVS");
            expected.setUserId("GitHub hook");
            expected.setHeadCommitSHA("edde69ecb8e8a88dde09fa9789e2c9cab7cf7cf9");
            expected.setAttempts(0);
        }


        @Test
        public void testGetGitHubWebhookConfig__ForkFalse() {
            // main test
            assertEquals(expected, LPVSWebhookUtil.getGitHubWebhookConfig(json_to_test));
        }
    }

    @Nested
    class TestCheckPayload {
        String json_to_test;

        @Test
        public void testCheckPayload() {
            // test initial `if`
            json_to_test =
                "{" +
                    "\"action\": \"opened\", " +
                    "\"zen\": \"test\"" +
                "}";
            assertFalse(LPVSWebhookUtil.checkPayload(json_to_test));

            // test the rest 6 cases of `LPVSPullRequestAction`
            json_to_test = "{\"action\": \"opened\"}";
            assertTrue(LPVSWebhookUtil.checkPayload(json_to_test));

            json_to_test = "{\"action\": \"reopened\"}";
            assertTrue(LPVSWebhookUtil.checkPayload(json_to_test));

            json_to_test = "{\"action\": \"synchronize\"}";
            assertTrue(LPVSWebhookUtil.checkPayload(json_to_test));

            json_to_test = "{\"action\": \"closed\"}";
            assertFalse(LPVSWebhookUtil.checkPayload(json_to_test));

            json_to_test = "{\"action\": \"rescan\"}";
            assertFalse(LPVSWebhookUtil.checkPayload(json_to_test));

            json_to_test = "{\"action\": \"any_of_above\"}";
            assertFalse(LPVSWebhookUtil.checkPayload(json_to_test));
        }
    }
}
