package com.tajchert.glassware.pjwstk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.tajchert.glassware.pjwstk.api.Zajecia;
import com.tajchert.glassware.pjwstk.api.getAPI;

public class LessonActivity extends Activity implements
		TextToSpeech.OnInitListener {

	private static final String LIVE_CARD_ID = "pjwstk_lesson_card";
	private List<Card> mCards = new ArrayList<Card>();
	private CardScrollView mCardScrollView;
	private ExampleCardScrollAdapter adapter;
	long minOne = Long.MAX_VALUE;
	Zajecia close;
	long minCurr = Long.MAX_VALUE;
	Zajecia closeEnd;
	private TextToSpeech tts;
	private Card card1;
	private LiveCard mLiveCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tts = new TextToSpeech(this, this);
		mCardScrollView = new CardScrollView(this);
		adapter = new ExampleCardScrollAdapter();
		mCardScrollView.setAdapter(adapter);
		mCardScrollView.activate();
		setContentView(mCardScrollView);
		createCards();

		//TimelineManager tm = TimelineManager.from(LessonActivity.this);
		new UserLoginTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCards != null && minOne != 0 && card1 != null
				&& !card1.getText().equals(getResources().getString(R.string.card_lesson_downloading))) {
			speakTillNextLesson();
		}
	}

	@Override
	public void onStop() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onStop();
	}

	private void createNoLessonCard() {
		mCards = new ArrayList<Card>();

		card1 = new Card(this);
		card1.setText(getResources().getString(R.string.card_lesson_no_lessons_week));
		card1.setFootnote("");
		card1.setImageLayout(Card.ImageLayout.LEFT);
		card1.addImage(R.drawable.logo_pjwstk_red);
		mCards.add(card1);
		adapter.notifyDataSetChanged();
	}

	private void createCards() {
		mCards = new ArrayList<Card>();

		card1 = new Card(this);
		// card1.setText("Nastêpne zajêcia:\n         "+GlassOwner.zajecia.firstEntry().getValue().getNazwa()+"\n         "
		// + GlassOwner.zajecia.firstEntry().getValue().date.getHours()); //
		// Main text area

		if (close != null) {
			String content = getResources().getString(R.string.card_lesson_next_lesson)
					+ close.date.getHours() + ":";
			int min = close.date.getMinutes();
			if (min < 10) {
				content += "0" + min;
			} else {
				content += min;
			}
			content += "\n            " + close.getKod();
			card1.setText(content); // Main text area
			String foot = close.getNazwa_sali() + "  " + close.getBudynek();
			int mintsOne = (int) (minOne / 60000);
			if (mintsOne < 60 && mintsOne > 0) {
				foot += ",  " + mintsOne + " min.";
			}
			card1.setFootnote(foot);

		} else {
			card1.setText("Pobieranie...");
			card1.setFootnote("");
		}
		card1.setImageLayout(Card.ImageLayout.LEFT);
		card1.addImage(R.drawable.logo_pjwstk_red);
		
		
		if (closeEnd != null) {
			Card card2 = new Card(this);
			int mints = (int) (minCurr / 60000);
			String content2 = getResources().getString(R.string.card_lesson_till_end) + mints + " min";
			card2.setText(content2);
			card2.setImageLayout(Card.ImageLayout.LEFT);
			card2.addImage(R.drawable.logo_pjwstk_red);
			mCards.add(card2);
		}
		
		mCards.add(card1);
		if(GlassOwner.zajeciaNotNeed != null && GlassOwner.zajeciaNotNeed.size()>0){
			Zajecia prev = close;
			for (Map.Entry<Long,Zajecia> entry : GlassOwner.zajecia.entrySet()) {
				if(entry.getKey() != close.date.getTime()){
					Zajecia zaj = entry.getValue();
					
					if(zaj.date.getDay() != prev.date.getDay()){
						Card cardWeekDay = new Card(this);
						String dayWeek = "Default";
						switch(zaj.date.getDay()){
							case 0:
								dayWeek = "Niedziela";
								break;
							case 1:
								dayWeek = "Poniedzialek";
								break;
							case 2:
								dayWeek = "Wtorek";
								break;
							case 3:
								dayWeek = "Sroda";
								break;
							case 4:
								dayWeek = "Czwartek";
								break;
							case 5:
								dayWeek = "Piatek";
								break;
							case 6:
								dayWeek = "Sobota";
								break;
						}
						dayWeek  += "  "+  zaj.date.getDate() + "." + (zaj.date.getMonth()+1);
						cardWeekDay.setText(dayWeek); // Main text area
						mCards.add(cardWeekDay);
					}
					prev = zaj;
					
					Card card3 = new Card(this);
					String content = getResources().getString(R.string.card_lesson_next_lesson)
							+ zaj.date.getHours() + ":";
					int min = zaj.date.getMinutes();
					if (min < 10) {
						content += "0" + min;
					} else {
						content += min;
					}
					content += "\n            " + zaj.getKod();
					card3.setText(content); // Main text area
					card3.setFootnote(zaj.getNazwa_sali() + "  " + zaj.getBudynek());;
					card3.setImageLayout(Card.ImageLayout.LEFT);
					card3.addImage(R.drawable.logo_pjwstk_red);
					mCards.add(card3);
				}
			}
		}

		
		adapter.notifyDataSetChanged();
		publishCard(LessonActivity.this);
	}

	private void speakTillNextLesson() {
		int hours = (int) ((minOne / (1000 * 60 * 60)));
		int minutes = (int) ((minOne / (1000 * 60)));
		if (hours > 0) {
			speakOut(getResources().getString(R.string.card_lesson_main_till_next) + hours + "hours");
		} else if (hours == 1) {
			speakOut(getResources().getString(R.string.card_lesson_main_till_next) + hours + " hour");
		} else if (hours == 0) {
			speakOut(getResources().getString(R.string.card_lesson_main_till_next) + minutes + " minutes");
		}
	}

	private void speakOut(String in) {
		tts.speak(in, TextToSpeech.QUEUE_FLUSH, null);
	}

	private class ExampleCardScrollAdapter extends CardScrollAdapter {
		@Override
		public int findIdPosition(Object id) {
			return -1;
		}
		@Override
		public int findItemPosition(Object item) {
			return mCards.indexOf(item);
		}
		@Override
		public int getCount() {
			return mCards.size();
		}
		@Override
		public Object getItem(int position) {
			return mCards.get(position);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return mCards.get(position).toView();
		}
	}

	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			//TODO paste here your student credentials
			getAPI.getNTLMResponse(GlassOwner.USERNAME, GlassOwner.PASSWORD);
			
			Date curr = new Date();
			//For testing purposes
			// curr.setHours(7);
			// curr.setMinutes(46);
			long diff;
			for (Map.Entry<Long,Zajecia> entry : GlassOwner.zajecia.entrySet()) {
				Zajecia zaj = entry.getValue();
				diff = zaj.date.getTime() - curr.getTime();
				if (diff < 0) {
					long diffEnd = zaj.endDate.getTime() - curr.getTime();
					if (diffEnd > 0 && diffEnd < minCurr) {
						closeEnd = zaj;
						minCurr = diffEnd;
					}
				} else if(diff > 0 && diff < minOne && !zaj.getKod().contains("HIS")
						&& !zaj.getKod().contains("NIM")) {
					//TODO add here lessons that you do not attend
					close = zaj;
					minOne = diff;
					Log.v("PJWSTK_GLASS", "" + close.getKod() + ", " + close.date.getHours());
				}else{
					GlassOwner.zajeciaNotNeed.add(zaj);
				}
		        //System.out.println("key ->" + entry.getKey() + ", value->" + entry.getValue());
		    }
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			
			
			if (close != null) {
				Log.d("PJWSTK_GLASS",
						"" + close.getKod() + ", " + close.date.getHours()
								+ ":" + close.date.getMinutes());
				createCards();
				if (mCards != null && minOne != 0) {
					speakTillNextLesson();
				}
			} else {
				//Put some card that there are no lessons in next 7 days
			}

		}

		@Override
		protected void onCancelled() {
		}
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				// btnSpeak.setEnabled(true);
				// speakOut();
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}

	}

	private void publishCard(Context context) {
		try {
			unpublishCard(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mLiveCard == null) {
			TimelineManager tm = TimelineManager.from(context);
			mLiveCard = tm.createLiveCard(LIVE_CARD_ID);

			// mLiveCard.setViews(new
			// RemoteViews(context.getPackageName(),R.layout.livecard_lesson));
			Intent intent = new Intent(context, LessonActivity.class);
			mLiveCard.setAction(PendingIntent
					.getActivity(context, 0, intent, 0));
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.livecard_lesson);
			// Log.e("PJWSTK_GLASS", content);

			remoteViews.setTextViewText(R.id.textViewContent,
					"PJWSTK\n\nNastêpne zajęcia");
			// remoteViews.setCharSequence(R.id.textViewContent, "setText",
			// content);
			mLiveCard.setViews(remoteViews);
			mLiveCard.publish(LiveCard.PublishMode.SILENT);
		} else {
			// Card is already published.
			return;
		}
	}

	private void unpublishCard(Context context) {
		if (mLiveCard != null) {
			mLiveCard.unpublish();
			mLiveCard = null;
		}
	}

}
