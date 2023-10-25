package com.example.storyapp.view.login

import android.content.Context
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.storyapp.JsonConverter
import com.example.storyapp.R
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.view.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    private val mockWebServer = MockWebServer()
    private val dummyEmail = "test34@gmail.com"
    private val dummyPassword = "1nya8kali"
    private val toContext = ApplicationProvider.getApplicationContext<Context>()
    private val resources: Resources = toContext.resources

    @get: Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp(){
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginSuccess(){
        onView(withId(R.id.edtEmailLogin)).perform(ViewActions.click())
        onView(withId(R.id.edtEmailLogin)).perform(typeText(dummyEmail), closeSoftKeyboard())

        onView(withId(R.id.edtPasswordLogin)).perform(ViewActions.click())
        onView(withId(R.id.edtPasswordLogin)).perform(typeText(dummyPassword), closeSoftKeyboard())

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("login_success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.btnLogin)).perform(ViewActions.click())
        onView(withText(resources.getString(R.string.next_activity))).perform(ViewActions.click())

        onView(withId(R.id.menu_Logout)).perform()
    }

    @After
    fun tearDown(){
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

}


