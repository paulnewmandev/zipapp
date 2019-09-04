package com.zipnoticiasec.ap.app.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.CommentModel;
import com.zipnoticiasec.ap.app.models.NoticeModel;
import com.zipnoticiasec.ap.app.ui.adapters.CommentsAdapter;
import com.zipnoticiasec.ap.app.utils.DialogUtils;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.zipnoticiasec.ap.app.utils.ApiUtils.ApiUrl;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.COMMENTS;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.DATA;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.GET_ARTICLE;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.MESSAGE;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.POST_COMMENT;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.RATING;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.STATUS;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.SUCCESS;
import static com.zipnoticiasec.ap.app.utils.AppUtils.showToast;
import static com.zipnoticiasec.ap.app.utils.FormUtils.dateToStringyyyyMMddTHHmmss;

public class ViewNoticeActivity extends AppCompatActivity {

    private Context mContext;
    private TextView mDay, mMonth;
    private ImageView mImage;
    private ImageView mPrevImage, mNextImage, mBack;
    private TextView mCounterReaders, mCategory, mTitle, mContent, mSource, mLabelSource;
    private RadioGroup mRadioRating;
    private ImageView mAddComment;
    private RecyclerView mRecyclerComments;
    private int currentPosition;
    private List<NoticeModel> mListNotices;
    private List<CommentModel> mListComments;
    private NoticeModel mModel;
    private int mRating;
    private RadioButton mRadButton, mRadButton2, mRadButton3, mRadButton4, mRadButton5;
    private View viewRanking, titleRanking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notice);
        mContext = this;
        initViews();
    }

    private void initViews(){
        mDay = findViewById(R.id.viewNoticeDay);
        mMonth = findViewById(R.id.viewNoticeMonth);
        mImage = findViewById(R.id.viewNoticeImage);
        mBack = findViewById(R.id.viewNoticeBack);
        mBack.setOnClickListener(v -> onBackPressed());
        mPrevImage = findViewById(R.id.viewNoticePrev);
        mNextImage = findViewById(R.id.viewNoticeNext);
        mCounterReaders = findViewById(R.id.viewNoticeReaders);
        mCategory = findViewById(R.id.viewNoticeCategory);
        mTitle = findViewById(R.id.viewNoticeTitle);
        mContent = findViewById(R.id.viewNoticeContent);
        mRadioRating = findViewById(R.id.viewNoticeRadGroup);
        mAddComment = findViewById(R.id.viewNoticeAddComment);
        mRecyclerComments = findViewById(R.id.simpleRecyclerView);
        currentPosition = getIntent().getIntExtra(SessionUtils.PARAMS.notice.name(), 0);
        mListNotices = SessionUtils.getBoletines(mContext);
        mRadButton = findViewById(R.id.viewNoticeRdButton);
        mRadButton2 = findViewById(R.id.viewNoticeRdButton2);
        mRadButton3 = findViewById(R.id.viewNoticeRdButton3);
        mRadButton4 = findViewById(R.id.viewNoticeRdButton4);
        mRadButton5 = findViewById(R.id.viewNoticeRdButton5);
        mSource = findViewById(R.id.viewNoticeSource);
        mLabelSource = findViewById(R.id.viewNoticeLabelSource);
        viewRanking = findViewById(R.id.viewNoticeViewRanking);
        titleRanking = findViewById(R.id.viewNoticeTitleRanking);
        getData();
    }

    private void getData(){
        MaterialDialog mDialog = DialogUtils.showProgress(mContext, getString(R.string.title_get_data), getString(R.string.content_get_data));
        String mUrl = ApiUrl+GET_ARTICLE+"?id_noticia="+mListNotices.get(currentPosition).id_noticia;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString(STATUS).contentEquals(SUCCESS)){
                        //Obtengo la info de la noticia
                        JSONArray mArray = mJson.getJSONArray(DATA);
                        String sData = mArray.getString(0);
                        Type mDataType = new TypeToken<NoticeModel>() {}.getType();
                        mModel = new Gson().fromJson(sData, mDataType);
                        //Obtengo la informacion del rating
                        JSONArray mRatingArray = mJson.getJSONArray(RATING);
                        JSONObject mRatingObject = mRatingArray.getJSONObject(0);
                        mRating = mRatingObject.getInt(RATING);
                        String sComments = mJson.getString(COMMENTS);
                        Type mCommentsType = new TypeToken<List<CommentModel>>() {}.getType();
                        mListComments = new Gson().fromJson(sComments, mCommentsType);
                        initActivity();
                    }else{
                        showToast(mContext, mJson.getString(MESSAGE));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(mContext, getString(R.string.error_json));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mDialog.dismiss();
                showToast(mContext, getString(R.string.error_server));
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });


    }

    private void initActivity(){
        String[] arrayFecha = mModel.fecha.split(" ");
        mDay.setText(arrayFecha[0]+", "+arrayFecha[1]);
        mMonth.setText(arrayFecha[2]+"\n"+arrayFecha[3]);
        Picasso.get().load(mModel.imagen).error(R.drawable.logo_color).into(mImage);
        mCounterReaders.setText(mModel.visitas+" lectores");
        mCategory.setText(mModel.categoria);
        mTitle.setText(mModel.titulo);
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(mModel.contenido, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(mModel.contenido);
        }
        //mContent.setText(result);
        //mContent.setMovementMethod(LinkMovementMethod.getInstance());



        Spannable spannable = new SpannableString(result);
        //Linkify.addLinks(spannable, Linkify.WEB_URLS);

        URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (URLSpan urlSpan : spans) {
            LinkSpan linkSpan = new LinkSpan(urlSpan.getURL());
            int spanStart = spannable.getSpanStart(urlSpan);
            int spanEnd = spannable.getSpanEnd(urlSpan);
            spannable.setSpan(linkSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.removeSpan(urlSpan);
        }

        mContent.setMovementMethod(EnhancedLinkMovementMethod.getInstance());

        mContent.setText(spannable, TextView.BufferType.SPANNABLE);

        //mContent.setText(Html.fromHtml(mModel.contenido));
        switch (mRating){
            case 0:
                mRadioRating.check(R.id.viewNoticeRdButton);
                break;
            case 1:
                mRadioRating.check(R.id.viewNoticeRdButton);
                break;
            case 2:
                mRadioRating.check(R.id.viewNoticeRdButton2);
                break;
            case 3:
                mRadioRating.check(R.id.viewNoticeRdButton3);
                break;
            case 4:
                mRadioRating.check(R.id.viewNoticeRdButton4);
                break;
            case 5:
                mRadioRating.check(R.id.viewNoticeRdButton5);
                break;
        }
        mRecyclerComments.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        CommentsAdapter mAdapter = new CommentsAdapter(mContext, mListComments);
        mRecyclerComments.setAdapter(mAdapter);
        if(mListNotices.size() > 1){
            if(currentPosition == 0){
                mPrevImage.setVisibility(View.GONE);
                mNextImage.setVisibility(View.VISIBLE);
            }else if(currentPosition == mListNotices.size() -1){
                mPrevImage.setVisibility(View.VISIBLE);
                mNextImage.setVisibility(View.GONE);
            }else{
                mPrevImage.setVisibility(View.VISIBLE);
                mNextImage.setVisibility(View.VISIBLE);
            }
        }else{
            mPrevImage.setVisibility(View.GONE);
            mNextImage.setVisibility(View.GONE);
        }

        if(mModel.fuente == null){
            mLabelSource.setVisibility(View.GONE);
            mSource.setVisibility(View.GONE);
        }else if(mModel.fuente.isEmpty()){
            mLabelSource.setVisibility(View.GONE);
            mSource.setVisibility(View.GONE);
        }


        mSource.setText(mModel.fuente);

        mSource.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, BrowserActivity.class);
            intent.putExtra("source", mModel.fuente);
            intent.putExtra("url", mModel.url_fuente);
            startActivity(intent);
        });

        mPrevImage.setOnClickListener(v -> {
            currentPosition--;
            getData();
        });

        mNextImage.setOnClickListener(v -> {
            currentPosition++;
            getData();
        });

        mAddComment.setOnClickListener(v -> dialogAddComment());

        viewRanking.setOnClickListener(v -> dialogAddComment());

        titleRanking.setOnClickListener(v -> dialogAddComment());

    }

    private void dialogAddComment(){
        MaterialDialog mDialog = DialogUtils.showCustomAcceptCancelDialog(mContext, "", R.layout.dialog_add_comment);
        View v = mDialog.getCustomView();
        EditText mComment = v.findViewById(R.id.dlgCommentText);
        RadioGroup mRdGroup = v.findViewById(R.id.dlgCommentRadGroup);
        mDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v1 -> {
            if(mComment.getText().toString().isEmpty()){
                mComment.setError(getString(R.string.error_field_required));
                return;
            }
            int rating = 0;
            switch (mRdGroup.getCheckedRadioButtonId()){
                case R.id.dlgCommentRdButton:
                    rating = 1;
                    break;
                case R.id.dlgCommentRdButton2:
                    rating = 2;
                    break;
                case R.id.dlgCommentRdButton3:
                    rating = 3;
                    break;
                case R.id.dlgCommentRdButton4:
                    rating = 4;
                    break;
                case R.id.dlgCommentRdButton5:
                    rating = 5;
                    break;
            }
            mDialog.dismiss();
            sendComment(mComment.getText().toString(), rating);
        });
    }

    private void sendComment(String comment, int rating){
        MaterialDialog mDialog = DialogUtils.showProgress(mContext, getString(R.string.title_send_data), getString(R.string.content_send_data));
        String mUrl = ApiUrl+POST_COMMENT;
        RequestParams mParams = new RequestParams();
        mParams.put("id_noticia", mListNotices.get(currentPosition).id_noticia);
        mParams.put("id_usuario", SessionUtils.getUser(mContext).id);
        mParams.put("fecha", dateToStringyyyyMMddTHHmmss(new Date()));
        mParams.put("comentario", comment);
        mParams.put("rating", rating);
        new AsyncHttpClient().post(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString(STATUS).contentEquals(SUCCESS)){
                        showToast(mContext, "Comentario agregado exitosamente");
                        getData();
                    }else{
                        showToast(mContext, mJson.getString(MESSAGE));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(mContext, getString(R.string.error_json));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mDialog.dismiss();
                showToast(mContext, getString(R.string.error_server));
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });

    }

    private class LinkSpan extends URLSpan {
        private LinkSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View view) {
            String url = getURL();
            if (mContext != null && url != null) {
                Intent intent = new Intent(mContext, BrowserActivity.class);
                intent.putExtra("source", url);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        }
    }

    public static class EnhancedLinkMovementMethod extends ArrowKeyMovementMethod {

        private static EnhancedLinkMovementMethod sInstance;

        private static Rect sLineBounds = new Rect();

        public static MovementMethod getInstance() {
            if (sInstance == null) {
                sInstance = new EnhancedLinkMovementMethod();
            }
            return sInstance;
        }

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {

                int index = getCharIndexAt(widget, event);
                if (index != -1) {
                    ClickableSpan[] link = buffer.getSpans(index, index, ClickableSpan.class);
                    if (link.length != 0) {
                        if (action == MotionEvent.ACTION_UP) {
                            link[0].onClick(widget);
                        }
                        else if (action == MotionEvent.ACTION_DOWN) {
                            Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                        }
                        return true;
                    }
                }
            /*else {
                Selection.removeSelection(buffer);
            }*/

            }

            return super.onTouchEvent(widget, buffer, event);
        }

        private int getCharIndexAt(TextView textView, MotionEvent event) {
            // get coordinates
            int x = (int) event.getX();
            int y = (int) event.getY();
            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();
            x += textView.getScrollX();
            y += textView.getScrollY();

            /*
             * Fail-fast check of the line bound.
             * If we're not within the line bound no character was touched
             */
            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            synchronized (sLineBounds) {
                layout.getLineBounds(line, sLineBounds);
                if (! sLineBounds.contains(x, y)) {
                    return -1;
                }
            }

            // retrieve line text
            Spanned text = (Spanned) textView.getText();
            int lineStart = layout.getLineStart(line);
            int lineEnd = layout.getLineEnd(line);
            int lineLength = lineEnd - lineStart;
            if (lineLength == 0) {
                return -1;
            }
            Spanned lineText = (Spanned) text.subSequence(lineStart, lineEnd);

            // compute leading margin and subtract it from the x coordinate
            int margin = 0;
            LeadingMarginSpan[] marginSpans = lineText.getSpans(0, lineLength, LeadingMarginSpan.class);
            if (marginSpans != null) {
                for (LeadingMarginSpan span : marginSpans) {
                    margin += span.getLeadingMargin(true);
                }
            }
            x -= margin;

            // retrieve text widths
            float[] widths = new float[lineLength];
            TextPaint paint = textView.getPaint();
            paint.getTextWidths(lineText, 0, lineLength, widths);

            // scale text widths by relative font size (absolute size / default size)
            final float defaultSize = textView.getTextSize();
            float scaleFactor = 1f;
            AbsoluteSizeSpan[] absSpans = lineText.getSpans(0, lineLength, AbsoluteSizeSpan.class);
            if (absSpans != null) {
                for (AbsoluteSizeSpan span : absSpans) {
                    int spanStart = lineText.getSpanStart(span);
                    int spanEnd = lineText.getSpanEnd(span);
                    scaleFactor = span.getSize() / defaultSize;
                    int start = Math.max(lineStart, spanStart);
                    int end = Math.min(lineEnd, spanEnd);
                    for (int i = start; i < end; i++) {
                        widths[i] *= scaleFactor;
                    }
                }
            }

            // find index of touched character
            float startChar = 0;
            float endChar = 0;
            for (int i = 0; i < lineLength; i++) {
                startChar = endChar;
                endChar += widths[i];
                if (endChar >= x) {
                    // which "end" is closer to x, the start or the end of the character?
                    int index = lineStart + (x - startChar < endChar - x ? i : i + 1);
                    //Logger.e(Logger.LOG_TAG, "Found character: " + (text.length()>index ? text.charAt(index) : ""));
                    return index;
                }
            }

            return -1;
        }
    }
}
