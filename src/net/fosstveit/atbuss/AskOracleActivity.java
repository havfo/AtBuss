package net.fosstveit.atbuss;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.fosstveit.atbuss.utils.Utils;

public class AskOracleActivity extends SherlockActivity {

	private EditText oracleQuestion = null;
	private Button oracleAsk = null;
	private TextView oracleAnswer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_ask_oracle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		oracleQuestion = (EditText) findViewById(R.id.ask_oracle_box);
		oracleAnswer = (TextView) findViewById(R.id.oracle_answer);

		oracleAsk = (Button) findViewById(R.id.ask_oracle);
		oracleAsk.setOnClickListener(askOracleButtonClick);

		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private OnClickListener askOracleButtonClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			getOracleAnswer(oracleQuestion.getText().toString());
		}
	};

	private void getOracleAnswer(String question) {
		GetOracleAnswer getOracleAnswer = new GetOracleAnswer(question);
		getOracleAnswer.execute();
	}

	private class GetOracleAnswer extends AsyncTask<String, Void, String> {

		private String answer;
		private String question;

		public GetOracleAnswer(String question) {
			this.question = question;
		}

		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
			oracleAnswer.setText("Spør orakelet...");
		}

		@Override
		protected String doInBackground(String... params) {
			answer = Utils.askOracle(question.replaceAll(" ", "%20"));
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			oracleAnswer.setText(answer);
			setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
		}
	}
}
