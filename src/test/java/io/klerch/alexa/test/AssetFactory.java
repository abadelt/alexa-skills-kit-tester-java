package io.klerch.alexa.test;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletResponseEnvelope;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.interfaces.audioplayer.AudioItem;
import com.amazon.speech.speechlet.interfaces.audioplayer.ClearBehavior;
import com.amazon.speech.speechlet.interfaces.audioplayer.PlayBehavior;
import com.amazon.speech.speechlet.interfaces.audioplayer.Stream;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.ClearQueueDirective;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.PlayDirective;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.StopDirective;
import com.amazon.speech.ui.*;
import com.amazonaws.services.lambda.runtime.*;
import io.klerch.alexa.test.client.AlexaSessionActor;
import io.klerch.alexa.test.client.AlexaClient;
import io.klerch.alexa.test.client.endpoint.*;
import org.joox.Match;
import org.w3c.dom.Document;

import java.util.Collections;
import java.util.Map;

import static org.joox.JOOX.$;

public class AssetFactory {
    public static final String DEFAULT_APP_ID = "app-id";
    public static final String DEFAULT_AP_PREVIOUS_TOKEN = "previous Token";
    public static final String DEFAULT_AP_URL = "https://stream/stream.mp3";
    public static final String DEFAULT_AP_TOKEN = "token";
    public static final String SESSION_KEY_WITH_INTENT_NAME = "intent";
    public static final long DEFAULT_AP_OFFSET = 1000L;

    public static final String DEFAULT_VERSION = AlexaClient.VERSION;
    public static final String DEFAULT_TEXT = "test";
    public static final String DEFAULT_CARD_TITLE = "card_title";
    public static final String DEFAULT_CARD_CONTENT = "card_content";
    public static final String DEFAULT_CARD_TEXT = "card_text";
    public static final String DEFAULT_CARD_SMALL_IMAGE = "https://img/small.jpg";
    public static final String DEFAULT_CARD_LARGE_IMAGE = "https://img/large.jpg";

    public static RequestStreamHandler getRequestStreamHandler() {
        return (inputStream, outputStream, context) -> {
        };
    }

    public static SpeechletResponseEnvelope givenResponseWithSsmlOutputSpeech() {
        return givenResponseWithSsmlOutputSpeech(DEFAULT_TEXT);
    }

    public static SpeechletResponseEnvelope givenResponseWithSsmlOutputSpeech(final String ssml) {
        return givenResponseWithSsmlOutputSpeech(ssml, false);
    }

    public static SpeechletResponseEnvelope givenResponseWithSsmlOutputSpeech(final String ssml, final boolean tell) {
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml(String.format("<speak>%s</speak>", ssml));
        final Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);
        return envelope(tell ? SpeechletResponse.newTellResponse(outputSpeech) : SpeechletResponse.newAskResponse(outputSpeech, reprompt));
    }

    public static SpeechletResponseEnvelope givenResponseWithSessionAttribute(final String key, final Object val) {
        final SpeechletResponseEnvelope envelope = givenResponseWithSsmlOutputSpeech();
        envelope.setSessionAttributes(Collections.singletonMap(key, val));
        return envelope;
    }

    public static SpeechletResponseEnvelope givenResponseWithPlayDirective(final PlayBehavior behavior) {
        return givenResponseWithPlayDirective(behavior, true);
    }

    public static SpeechletResponseEnvelope givenResponseWithPlayDirective(final PlayBehavior behavior, final boolean withAudioItem) {
        SpeechletResponseEnvelope response = givenResponseWithSsmlOutputSpeech();

        final PlayDirective playDirective = new PlayDirective();
        playDirective.setPlayBehavior(behavior);

        if (withAudioItem) {
            final Stream stream = new Stream();
            stream.setExpectedPreviousToken(DEFAULT_AP_PREVIOUS_TOKEN);
            stream.setOffsetInMilliseconds(DEFAULT_AP_OFFSET);
            stream.setToken(DEFAULT_AP_TOKEN);
            stream.setUrl(DEFAULT_AP_URL);
            final AudioItem audioItem = new AudioItem();
            audioItem.setStream(stream);
            playDirective.setAudioItem(audioItem);
        }

        response.getResponse().setDirectives(Collections.singletonList(playDirective));
        return response;
    }

    public static SpeechletResponseEnvelope givenResponseWithStopDirective() {
        SpeechletResponseEnvelope response = givenResponseWithSsmlOutputSpeech();
        response.getResponse().setDirectives(Collections.singletonList(new StopDirective()));
        return response;
    }

    public static SpeechletResponseEnvelope givenResponseWithClearQueueDirective(final ClearBehavior behavior) {
        SpeechletResponseEnvelope response = givenResponseWithSsmlOutputSpeech();
        ClearQueueDirective clearQueueDirective = new ClearQueueDirective();
        clearQueueDirective.setClearBehavior(behavior);
        response.getResponse().setDirectives(Collections.singletonList(clearQueueDirective));
        return response;
    }

    public static SpeechletResponseEnvelope givenResponseWithPlainOutputSpeech() {
        return givenResponseWithPlainOutputSpeech(DEFAULT_TEXT);
    }

    public static SpeechletResponseEnvelope givenResponseWithPlainOutputSpeech(final String text) {
        return givenResponseWithPlainOutputSpeech(text, false);
    }

    public static SpeechletResponseEnvelope givenResponseWithPlainOutputSpeech(final String text, final boolean tell) {
        final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(text);
        final Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);
        return envelope(tell ? SpeechletResponse.newTellResponse(outputSpeech) : SpeechletResponse.newAskResponse(outputSpeech, reprompt));
    }

    public static SpeechletResponseEnvelope givenResponseWithSimpleCard() {
        return givenResponseWithSimpleCard(DEFAULT_CARD_TITLE, DEFAULT_CARD_CONTENT);
    }

    public static SpeechletResponseEnvelope givenResponseWithSimpleCard(final String title, final String content) {
        final SimpleCard card = new SimpleCard();
        card.setContent(content);
        card.setTitle(title);
        final SpeechletResponseEnvelope response = givenResponseWithSsmlOutputSpeech();
        response.getResponse().setCard(card);
        return response;
    }

    public static SpeechletResponseEnvelope givenResponseWithLinkAccountCard() {
        return givenResponseWithLinkAccountCard(DEFAULT_CARD_TITLE);
    }

    public static SpeechletResponseEnvelope givenResponseWithLinkAccountCard(final String title) {
        final LinkAccountCard card = new LinkAccountCard();
        card.setTitle(title);
        final SpeechletResponseEnvelope response = givenResponseWithSsmlOutputSpeech();
        response.getResponse().setCard(card);
        return response;
    }

    public static SpeechletResponseEnvelope givenResponseWithStandardCard() {
        return givenResponseWithStandardCard(DEFAULT_CARD_TITLE, DEFAULT_CARD_TEXT);
    }

    public static SpeechletResponseEnvelope givenResponseWithStandardCard(final String title, final String text) {
        final Image image = new Image();
        image.setSmallImageUrl(DEFAULT_CARD_SMALL_IMAGE);
        image.setLargeImageUrl(DEFAULT_CARD_LARGE_IMAGE);
        return givenResponseWithStandardCard(title, text, image);
    }

    public static SpeechletResponseEnvelope givenResponseWithStandardCard(final String title, final String text, final Image image) {
        final StandardCard card = new StandardCard();
        card.setText(text);
        card.setTitle(title);
        card.setImage(image);
        final SpeechletResponseEnvelope response = givenResponseWithSsmlOutputSpeech();
        response.getResponse().setCard(card);
        return response;
    }

    private static SpeechletResponseEnvelope envelope(final SpeechletResponse response) {
        final SpeechletResponseEnvelope envelope = new SpeechletResponseEnvelope();
        envelope.setResponse(response);
        envelope.setVersion(DEFAULT_VERSION);
        return envelope;
    }

    public static AlexaClient givenClient() throws Exception {
        return AlexaClient.create(givenRequestStreamHandlerEndpoint(), DEFAULT_APP_ID).build();
    }

    public static AlexaClient givenClient(final String appId) throws Exception {
        return AlexaClient.create(givenRequestStreamHandlerEndpoint(), appId).build();
    }

    public static AlexaSessionActor givenActor() throws Exception {
        return new AlexaSessionActor(givenClient());
    }

    public static RequestStreamHandler givenRequestStreamHandlerThatReturns(final SpeechletResponseEnvelope response) {
        return (inputStream, outputStream, context) -> {
            final SpeechletRequestEnvelope envelope = SpeechletRequestEnvelope.fromJson(inputStream);
            final Map<String, Object> attributes = envelope.getSession().getAttributes();
            // set intent as session attribute
            if (envelope.getRequest() instanceof IntentRequest) {
                final IntentRequest intentRequest = (IntentRequest)envelope.getRequest();
                attributes.put(SESSION_KEY_WITH_INTENT_NAME, intentRequest.getIntent().getName());
                // set slots as session attributes
                if (intentRequest.getIntent().getSlots() != null) {
                    intentRequest.getIntent().getSlots().forEach(attributes::put);
                }
            } else if (envelope.getRequest() instanceof LaunchRequest) {
                attributes.put(SESSION_KEY_WITH_INTENT_NAME, "launch");
            }
            // set session attribute
            response.setSessionAttributes(attributes);
            response.toJson(outputStream);
        };
    }

    public static RequestStreamHandler givenRequestStreamHandlerThatReturns(final SpeechletResponseEnvelope response, final String sessionAttributeKey, final Object sessionAttributeVal) {
        return (inputStream, outputStream, context) -> {
            final SpeechletRequestEnvelope envelope = SpeechletRequestEnvelope.fromJson(inputStream);
            final Map<String, Object> attributes = envelope.getSession().getAttributes();
            attributes.put(sessionAttributeKey, sessionAttributeVal);
            // set intent as session attribute
            if (envelope.getRequest() instanceof IntentRequest) {
                final IntentRequest intentRequest = (IntentRequest)envelope.getRequest();
                attributes.put(SESSION_KEY_WITH_INTENT_NAME, intentRequest.getIntent().getName());
                // set slots as session attributes
                if (intentRequest.getIntent().getSlots() != null) {
                    intentRequest.getIntent().getSlots().forEach(attributes::put);
                }
            } else if (envelope.getRequest() instanceof LaunchRequest) {
                attributes.put(SESSION_KEY_WITH_INTENT_NAME, "launch");
            }
            // set session attribute
            response.setSessionAttributes(attributes);
            response.toJson(outputStream);
        };
    }

    public static AlexaLambdaEndpoint givenLambdaEndpoint() throws Exception {
        return (AlexaLambdaEndpoint)givenEndpointMatch("script-lambdaFunction.xml");
    }

    public static AlexaRequestStreamHandlerEndpoint givenRequestStreamHandlerEndpoint() throws Exception {
        return (AlexaRequestStreamHandlerEndpoint)givenEndpointMatch("script-requestStreamHandler.xml");
    }

    public static Context givenContext() {
        return new Context() {
            @Override
            public String getAwsRequestId() {
                return null;
            }

            @Override
            public String getLogGroupName() {
                return null;
            }

            @Override
            public String getLogStreamName() {
                return null;
            }

            @Override
            public String getFunctionName() {
                return null;
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {
                return 0;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return null;
            }
        };
    }

    private static AlexaEndpoint givenEndpointMatch(final String scriptFile) throws Exception {
        final Document doc = $(AssetFactory.class.getClassLoader().getResourceAsStream(scriptFile)).document();
        final Match mEndpoint = $(doc).first().find("configuration").find("endpoint");
        return AlexaEndpointFactory.createEndpoint(mEndpoint);
    }
}
