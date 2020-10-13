package org.hyperledger.indy.sdk.ledger;

import android.util.Log;

import org.hyperledger.indy.sdk.IndyIntegrationTest;
import org.hyperledger.indy.sdk.JsonTestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class AuthRuleRequestsTest extends IndyIntegrationTest {

	private String txnType = "NYM";
	private String authTypeCode = "1";
	private String addAuthAction = "ADD";
	private String editAuthAction = "EDIT";
	private String field = "role";
	private String oldValue = "0";
	private String newValue = "101";
	private JSONObject constraint;

	{
		try {
			constraint = new JSONObject()
						.put("sig_count", 1)
						.put("role", "0")
						.put("constraint_id", "ROLE")
						.put("need_to_be_owner", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBuildAuthRuleRequestWorksForAddAction() throws Exception {
		JSONObject expectedResult = new JSONObject()
				.put("identifier", DID)
				.put("operation",
						new JSONObject()
								.put("type", "120")
								.put("auth_type", authTypeCode)
								.put("auth_action", addAuthAction)
								.put("field", field)
								.put("new_value", newValue)
								.put("constraint", constraint)
				);

		String request = Ledger.buildAuthRuleRequest(DID, txnType, addAuthAction, field, null, newValue, constraint.toString()).get();

		assert (JsonTestUtils.toJsonMap(request).entrySet()
				.containsAll(
						JsonTestUtils.toJsonMap(expectedResult).entrySet()));
	}

	@Test
	public void testBuildAuthRuleRequestWorksForEditAction() throws Exception {
		JSONObject expectedResult = new JSONObject()
				.put("identifier", DID)
				.put("operation",
						new JSONObject()
								.put("type", "120")
								.put("auth_type", authTypeCode)
								.put("auth_action", editAuthAction)
								.put("field", field)
								.put("old_value", oldValue)
								.put("new_value", newValue)
								.put("constraint", constraint)
				);

		String request = Ledger.buildAuthRuleRequest(DID, txnType, editAuthAction, field, oldValue, newValue, constraint.toString()).get();

		assert (JsonTestUtils.toJsonMap(request).entrySet()
				.containsAll(
						JsonTestUtils.toJsonMap(expectedResult).entrySet()));
	}

	@Test
	public void testBuildGetAuthRuleRequestWorksForAddAction() throws Exception {
		JSONObject expectedResult = new JSONObject()
				.put("identifier", DID)
				.put("operation",
						new JSONObject()
								.put("type", "121")
								.put("auth_type", authTypeCode)
								.put("auth_action", addAuthAction)
								.put("field", field)
								.put("new_value", newValue)
				);

		String request = Ledger.buildGetAuthRuleRequest(DID, txnType, addAuthAction, field, null, newValue).get();

		assert (JsonTestUtils.toJsonMap(request).entrySet()
				.containsAll(
						JsonTestUtils.toJsonMap(expectedResult).entrySet()));
	}

	@Test
	public void testBuildAuthRulesRequestWorks() throws Exception {
		ArrayList<JSONObject> data = new ArrayList<>();

		data.add(new JSONObject()
				.put("auth_type", txnType)
				.put("auth_action", addAuthAction)
				.put("field", field)
				.put("new_value", newValue)
				.put("constraint", constraint)
		);

		data.add(new JSONObject()
				.put("auth_type", txnType)
				.put("auth_action", editAuthAction)
				.put("field", field)
				.put("old_value", oldValue)
				.put("new_value", newValue)
				.put("constraint", constraint)
		);

		JSONObject expectedResult = new JSONObject()
				.put("identifier", DID)
				.put("operation",
						new JSONObject()
								.put("type", "122")
								.put("rules", data)
				)
				// TODO are bottom 2 fields expected?
				.put("reqId", "id")
				.put("protocolVersion", 2);

		String request = Ledger.buildAuthRulesRequest(DID, data.toString()).get();

		Log.v("Indy-Test-Logs", "test_debug_separator");
		Log.v("Indy Test Logs", request);
		Log.v("Indy Test Logs", expectedResult.toString());

		assert (JsonTestUtils.toJsonMap(request).entrySet()
				.containsAll(
						JsonTestUtils.toJsonMap(expectedResult).entrySet()));
	}
}
