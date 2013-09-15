package com.squareup.shipfaster.cart;

import android.app.Activity;
import android.content.Intent;
import com.squareup.shipfaster.auth.AuthActivity;
import com.squareup.shipfaster.base.ShipFasterModule;
import com.squareup.shipfaster.base.ShipFasterApplication;
import com.squareup.shipfaster.settings.Settings;
import com.squareup.shipfaster.swipe.Card;
import com.squareup.shipfaster.swipe.SwipeEvent;
import dagger.ObjectGraph;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class CartTest {

  @Inject Cart cart;
  @Inject Settings settings;
  private Activity activity;

  @Before public void setUp() {
    ShipFasterApplication application = new ShipFasterApplication();
    activity = new Activity();
    application.setResumedActivity(activity);
    ObjectGraph.create(new ShipFasterModule(application), new CartTestModule()).inject(this);
    //settings = mock(Settings.class);
    //cart = new Cart(settings);
  }

  @Test public void can_buy_banana_with_card() {
    when(settings.acceptsCreditCards()).thenReturn(true);

    cart.addItem(Item.newBanana());
    assertThat(cart.canSwipeCard()).isTrue();
  }

  @Test public void auth_when_swipe_success() {
    prepareCanSwipe();

    cart.onSwipe(new SwipeEvent(true, Card.fakeCard()));

    Intent intent = shadowOf(activity).peekNextStartedActivity();
    assertThat(intent.getComponent().getClassName()).isEqualTo(AuthActivity.class.getName());
  }

  @Test public void no_auth_when_swipe_failed() {
    prepareCanSwipe();

    cart.onSwipe(new SwipeEvent(false, null));

    assertThat(shadowOf(activity).peekNextStartedActivity()).isNull();
  }

  private void prepareCanSwipe() {
    when(settings.acceptsCreditCards()).thenReturn(true);
    cart.addItem(Item.newBanana());
  }
}
