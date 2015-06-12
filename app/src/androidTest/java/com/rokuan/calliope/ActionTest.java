package com.rokuan.calliope;


import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.*;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.test.ActivityInstrumentationTestCase2;

import com.rokuan.calliope.source.SourceObject;
import com.rokuan.calliope.source.TextSource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by LEBEAU Christophe on 11/06/15.
 */
public class ActionTest extends ActivityInstrumentationTestCase2<HomeActivity> {
    private HomeActivity mActivity;

    public ActionTest(){
        super(HomeActivity.class);
    }

    @Before
    public void setUp()throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }

    private void openTextForm(){
        ViewInteraction importText = onView(withId(R.id.action_import_text));
        importText.perform(click());
    }

    private void closeTextForm(){
        ViewInteraction importText = onView(withId(R.id.action_import_text));
        importText.perform(click());
    }

    public static Matcher<Object> withContent(final String text) {
        return new BoundedMatcher<Object, SourceObject>(SourceObject.class) {
            @Override
            public boolean matchesSafely(SourceObject obj) {
                return obj.getType() == SourceObject.ObjectType.TEXT && ((TextSource)obj).getText().equals(text);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("'" + text + "'");
            }
        };
    }

    @Test
    public void testOpenAndCloseTextForm(){
        openTextForm();
        // Check that the form is visible after the click
        onView(withId(R.id.text_form))
                .check(matches(isDisplayed()));
        closeTextForm();
        onView(withId(R.id.text_form))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void testSubmitButtonActivation(){
        openTextForm();

        ViewInteraction submitButton = onView(withId(R.id.add_text_source));
        ViewInteraction editText = onView(withId(R.id.text_source_content));

        // Check that the submit button is disabled due to the absence of text in the EditText
        submitButton.check(matches(not(isEnabled())));
        // We type in some text
        editText.perform(typeText("Ceci est un message"), ViewActions.closeSoftKeyboard());
        // We now check that the submit button is enabled
        submitButton.check(matches(isEnabled()));
        // We clear the text
        editText.perform(clearText());
        // The button should be disabled
        submitButton.check(matches(not(isEnabled())));

        closeTextForm();
    }

    @Test
    public void testAddText() {
        openTextForm();

        ViewInteraction submitButton = onView(withId(R.id.add_text_source));
        ViewInteraction editText = onView(withId(R.id.text_source_content));
        String textToSubmit = "Text to submit";

        editText.perform(typeText(textToSubmit), ViewActions.closeSoftKeyboard());
        // Submit the text
        submitButton.perform(click());
        // The form should now be closed
        onView(withId(R.id.text_form))
                .check(matches(not(isDisplayed())));
        // TODO: A view should be added with the content of the EditText
        onData(allOf(is(instanceOf(SourceObject.class))))
                .inAdapterView(withId(R.id.messages_list))
                .atPosition(0)
                .check(matches(withContent(textToSubmit)));
    }
}
