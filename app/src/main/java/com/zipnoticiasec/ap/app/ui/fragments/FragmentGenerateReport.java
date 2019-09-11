package com.zipnoticiasec.ap.app.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zipnoticiasec.ap.app.R;
import com.zipnoticiasec.ap.app.models.CategoryModel;
import com.zipnoticiasec.ap.app.models.ReportModel;
import com.zipnoticiasec.ap.app.ui.activities.MainActivity;
import com.zipnoticiasec.ap.app.ui.adapters.CategoryAdapter;
import com.zipnoticiasec.ap.app.utils.DialogUtils;
import com.zipnoticiasec.ap.app.utils.SessionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.alexrs.prefs.lib.Prefs;

import static android.app.Activity.RESULT_OK;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.ApiUrl;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.DATA;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.GET_CATEGORIES;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.MESSAGE;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.POST_REPORT;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.STATUS;
import static com.zipnoticiasec.ap.app.utils.ApiUtils.SUCCESS;
import static com.zipnoticiasec.ap.app.utils.AppUtils.showToast;

/**
 * Created by Andres on 16/6/2019.
 */

public class FragmentGenerateReport extends Fragment {

    public static final int RECORD_VIDEO_REQUEST = 1005;
    public static final int REQUEST_IMAGE_CAPTURE = 1006;
    public static final int REQUEST_GALERY_PHOTO = 1007;

    private View view;
    private ImageView mGetFile, mRotateImage;
    private EditText mDescription, mPlace;
    public TextView mSelectCategory;
    private Button mSendReport;
    private ReportModel mModel;
    private List<CategoryModel> mListCategories;
    private String mCurrentFilePath = "";
    private int typeFile = 0; //0 noFile, 1 videoFile, 2 pictureFile
    public int mCategorySelected = -1;
    private float currentAngle = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_generate_report, container, false);
        initViews();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALERY_PHOTO && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mCurrentFilePath = cursor.getString(columnIndex);
            cursor.close();
            final InputStream imageStream;
            try {
                imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                final Bitmap btmpImage = BitmapFactory.decodeStream(imageStream);
                mGetFile.setImageBitmap(btmpImage);
                //mRotateImage.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                showToast(getContext(), "Error al obtener vista previa de la imagen");
            }
            showToast(getContext(), "Ha seleccionado un archivo para compartir");

        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse("file:"+mCurrentFilePath));
                mGetFile.setImageBitmap(photo);
                //mRotateImage.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
                showToast(getContext(), "Error al obtener vista previa de la imagen");
            }
            showToast(getContext(), "Ha seleccionado un archivo para compartir");
        }else if(requestCode == RECORD_VIDEO_REQUEST && resultCode == RESULT_OK){
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mCurrentFilePath, MediaStore.Images.Thumbnails.MINI_KIND);
            mGetFile.setImageBitmap(thumb);
            mRotateImage.setVisibility(View.GONE);
            showToast(getContext(), "Ha seleccionado un archivo para compartir");
        }else{
            mGetFile.setImageResource(R.drawable.upload);
            showToast(getContext(), "No se ha seleccionado ningun archivo");
            mCurrentFilePath = "";
            mRotateImage.setVisibility(View.GONE);
        }
    }

    private void initViews(){
        mGetFile = view.findViewById(R.id.frgGenReportUpload);
        mRotateImage = view.findViewById(R.id.frgGenReportRotateImage);
        mDescription = view.findViewById(R.id.frgGenReportContent);
        mPlace = view.findViewById(R.id.frgGenReportPlace);
        mSelectCategory = view.findViewById(R.id.frgGenReportCategory);
        mSendReport = view.findViewById(R.id.frgGenReportSend);
        mModel = new ReportModel();
        getCategories();
        initFragment();
    }

    private void initFragment(){
        mGetFile.setOnClickListener(v -> checkPermissions());
        //mRotateImage.setOnClickListener(v -> rotateImage());
        mListCategories = SessionUtils.getCategories(getContext());
        mSelectCategory.setOnClickListener(v -> dialogSelectCategories());
        mSendReport.setOnClickListener(v -> {
            if(mDescription.getText().toString().isEmpty()){
                mDescription.setError(getString(R.string.error_field_required));
                return;
            }

            if(mPlace.getText().toString().isEmpty()){
                mPlace.setError(getString(R.string.error_field_required));
                return;
            }

            if(mCurrentFilePath.isEmpty()){
                showToast(getContext(), "Debe seleccionar un archivo para compartir");
                return;
            }
            sendReport();
        });
    }

    private void getCategories(){
        String mUrl = ApiUrl+GET_CATEGORIES;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString(STATUS).contentEquals(SUCCESS)){
                        String sData = mJson.getString(DATA);
                        if(!Prefs.with(getContext()).getString(SessionUtils.PREFS.categories_data.name(), "").contentEquals(sData)){
                            Prefs.with(getContext()).save(SessionUtils.PREFS.categories_data.name(), sData);
                            initFragment();
                        }
                    }else{
                        showToast(getContext(), mJson.getString(MESSAGE));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(getContext(), getString(R.string.error_json));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                showToast(getContext(), getString(R.string.error_server));
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void checkPermissions(){
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            dialogGetMedia();
                        }else{
                            Toast.makeText(getContext(), getString(R.string.error_permissions_req), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();

    }

    private void dialogGetMedia(){
        MaterialDialog mDialog = DialogUtils.showListDialog("Seleccione", R.array.options_select_media, getContext());
        mDialog.getListView().setOnItemClickListener((parent, view, position, id) -> {
            switch (position){
                case 0: //Record video
                    dispatchTakeVideoIntent();
                    break;
                case 1: //Take photo
                    dispatchTakePictureIntent();
                    break;
                case 2: //pick image gallery
                    getPhotoGalery();
                    break;
            }
            mDialog.dismiss();
        });

    }

    private void dialogSelectCategories(){
        MaterialDialog mDialog = DialogUtils.showCustomDialog(getContext(), R.layout.dialog_select_categories);
        RecyclerView recyclerView = mDialog.getCustomView().findViewById(R.id.simpleRecyclerView);
        CategoryAdapter mAdapter = new CategoryAdapter(getContext(), this, mListCategories, mDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);

    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                Uri outputFileUri = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName() +
                        ".provider", videoFile);
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20);
                cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, RECORD_VIDEO_REQUEST);
            }
        }
    }

    //CREAR VIDEO
    private File createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "VIDEO_ZIP_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File video = File.createTempFile(
                videoFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentFilePath = video.getAbsolutePath();
        return video;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri outputFileUri = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName() +
                        ".provider", photoFile);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //CREAR IMAGEN
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_ZIP_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentFilePath = image.getAbsolutePath();
        return image;
    }

    private void getPhotoGalery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_GALERY_PHOTO);
    }

    private void sendReport() {
        MaterialDialog mDialog = DialogUtils.showProgressCount(getContext(), getString(R.string.title_send_data), getString(R.string.content_send_data));
        String mUrl = ApiUrl+POST_REPORT;
        RequestParams mParams = new RequestParams();
        mParams.put("id_usuario", SessionUtils.getUser(getContext()).id);
        mParams.put("lugar", mPlace.getText().toString());
        mParams.put("descripcion", mDescription.getText().toString());
        Log.d("url", mUrl);
        Log.d("id_usuario", String.valueOf(SessionUtils.getUser(getContext()).id));
        Log.d("lugar", mPlace.getText().toString());
        Log.d("descripcion", mDescription.getText().toString());
        Log.d("file_path", mCurrentFilePath);
        try {
            mParams.put("multimedia", new File(mCurrentFilePath));
            Log.d("Monitoreando archivo", "Archivo Cargado correctamente");
        } catch (FileNotFoundException e) {
            Log.d("Monitoreando archivo", "Error cargano archivo");
            e.printStackTrace();
            showToast(getContext(), "Error al cargar archivo");
        }
        //mParams.put("id_categoria", mListCategories.get(mCategorySelected).id);

        new AsyncHttpClient().post(mUrl, mParams, new BaseJsonHttpResponseHandler() {
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                Double mTotal = ((double) bytesWritten/totalSize) * 100;
                if(mDialog != null){
                    mDialog.setProgress(mTotal.intValue());
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                Log.d("Respuesta server", rawJsonResponse);
                mDialog.dismiss();
                try {
                    JSONObject mJson = new JSONObject(rawJsonResponse);
                    if(mJson.getString(STATUS).contentEquals(SUCCESS)){
                        dialogSuccess();
                    }else{
                        showToast(getContext(), mJson.getString(MESSAGE));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(getContext(), getString(R.string.error_json));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mDialog.dismiss();
                Log.d("Respuesta server", "Fallo el envio");
                Log.d("error", throwable.getMessage());
                if(rawJsonData != null){
                    Log.d("json error", rawJsonData);
                }
                dialogSuccess();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void dialogSuccess(){
        MaterialDialog mDialog = DialogUtils.showContentDialog(getActivity(), "Resultado",
                "¡Eres de los nuestros! \n Gracias por enviar tu reporte. \n Estará disponible al ser aprobado por el equipo ZIP.");
        mDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
            mDialog.dismiss();
            MainActivity mActivity = (MainActivity) getActivity();
            FragmentCitizenReport mFragment = new FragmentCitizenReport();
            Bundle args = new Bundle();
            args.putBoolean(SessionUtils.PARAMS.is_call_fragment_report.name(), true);
            mFragment.setArguments(args);
            mActivity.setFragment(getString(R.string.title_citizen_report), mFragment);
            mActivity.mReport.setVisibility(View.GONE);
            mActivity.mUpload.setVisibility(View.VISIBLE);
        });
    }


}
