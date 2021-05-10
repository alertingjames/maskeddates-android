package com.date.maskeddates.main;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.date.maskeddates.R;
import com.date.maskeddates.adapters.QuestionsListAdapter;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.models.Questionnaire;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.Locale;

public class QuestionsActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {

    ImageView searchButton, cancelButton, imvback;
    LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView title;
    SwipyRefreshLayout ui_RefreshLayout;
    private ProgressDialog _progressDlg;
    ListView listView;
    QuestionsListAdapter adapter = new QuestionsListAdapter(this);
    ArrayList<Questionnaire> questionnaires = new ArrayList<>();

    LinearLayout answerFrame, layout;
    TextView questionArea;
    EditText answerInputArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        searchBar = (LinearLayout)findViewById(R.id.search_bar);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        cancelButton = (ImageView)findViewById(R.id.cancelButton);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ui_edtsearch.getText().toString().toLowerCase(Locale.getDefault());
                if (text.length() != 0) {
                    adapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    adapter.setDatas(questionnaires);
                    listView.setAdapter(adapter);
                }

            }
        });
        listView = (ListView)findViewById(R.id.list);
        getData();

        answerFrame = (LinearLayout)findViewById(R.id.answerFrame);
        layout = (LinearLayout)findViewById(R.id.layout);
        questionArea = (TextView)findViewById(R.id.question);
        answerInputArea = (EditText)findViewById(R.id.answer);

    }

    private void getData(){
        String[] texts = {"My Self-Summary", "What I'm doing with my life", "I want to be better at", "The first thing people notice about me"};
        for (int i=0; i<texts.length; i++){
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.set_text(texts[i]);
            questionnaire.set_isactive("Inactive");
            questionnaires.add(questionnaire);
        }

        adapter.setDatas(questionnaires);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    public void searchButton(View view){
        cancelButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.GONE);
        searchBar.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
    }

    public void cancelButton(View view){
        cancelButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchBar.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
    }

    public void back(View view){
        finish();
    }

    public void answer(String question) {

        answerFrame.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        questionArea.setText(question);

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setTitle("Please answer to the quesion");
//        LayoutInflater inflater = getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.question_dialog, null);
//        final TextView questionArea = (TextView) dialogView.findViewById(R.id.question);
//        questionArea.setText(question);
//        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
//        questionArea.setTypeface(font);
//        final EditText answer = (EditText) dialogView.findViewById(R.id.inputBox);
//        builder.setView(dialogView);
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if(answer.length()>0){
//                    Commons.holder.answer.setVisibility(View.VISIBLE);
//                    Commons.holder.answer.setText(answer.getText().toString());
//                    Commons.holder.isactive.setVisibility(View.GONE);
//                    Commons.holder.checkMark.setVisibility(View.VISIBLE);
//                }else Toast.makeText(getApplicationContext(), "Please answer to this question", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        AlertDialog alert = builder.create();
//        alert.show();
    }

    public void cancelAnswer(View view){
        answerFrame.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
    }

    public void submit(View view){
        answerFrame.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        String answerText = answerInputArea.getText().toString();
        if(answerText.length()>0){
            Commons.holder.answer.setVisibility(View.VISIBLE);
            Commons.holder.answer.setText(answerText);
            Commons.holder.isactive.setVisibility(View.GONE);
            Commons.holder.checkMark.setVisibility(View.VISIBLE);
        }else Toast.makeText(getApplicationContext(), "Please answer to this question", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }
}




































