package util

import org.hamcrest.StringDescription
import org.json.JSONArray
import org.json.JSONObject
import org.junit.ComparisonFailure
import uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs

class JsonAssertion(private val actual: String) {
    private var allowingAnyArrayOrdering = false
    private var allowingExtraUnexpectedFields = false

    fun allowingAnyArrayOrdering(): JsonAssertion {
        allowingAnyArrayOrdering = true
        return this
    }

    fun isEqualTo(expected: String) {
        val expectedJson = toJsonString(expected)
        val actualJson = toJsonString(actual)

        var matcher = sameJSONAs(expectedJson)

        if (allowingAnyArrayOrdering) matcher = matcher.allowingAnyArrayOrdering()
        if (allowingExtraUnexpectedFields) matcher = matcher.allowingExtraUnexpectedFields()

        if (!matcher.matches(actualJson)) {
            val description = StringDescription()
            description
                .appendText("\nActual: $actualJson")
                .appendText("\nExpected: $expectedJson")
                .appendText("\nBut: ")

            matcher.describeMismatch(actualJson, description)
            throw ComparisonFailure(
                description.toString() + "\n",
                toJsonStringIndented(expected, 2),
                toJsonStringIndented(actual, 2)
            )
        }
    }

    companion object {

        fun assertThatJson(actualJson: String?): JsonAssertion {
            if (actualJson == null) throw AssertionError("actualJson must not be null")
            return JsonAssertion(actualJson)
        }

        private fun toJsonString(json: String): String {
            return toJsonStringIndented(json, 0)
        }

        private fun toJsonStringIndented(json: String, indentation: Int): String {
            if (json.startsWith("{"))
                return JSONObject(json).toString(indentation)

            if (json.startsWith("["))
                return JSONArray(json).toString(indentation)

            throw AssertionError("Not valid json expected value$json")
        }
    }

}