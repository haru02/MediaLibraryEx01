package com.example.froze.medialibraryex01;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener{

    private final static int REQUEST_CODE = 100;
    public static ArrayList<MusicData> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        datas = getMusicInfo();
        Log.i("MusicData", "-----------------뮤직데이터 입력 완료");
        setContentView(R.layout.activity_main);

        // 권한 세팅
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            datas = getMusicInfo();
            ListFragment lf = new ListFragment();}
        else
            checkPermissions();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            String permissionArray[] = { Manifest.permission.READ_EXTERNAL_STORAGE };
            requestPermissions( permissionArray , REQUEST_CODE );
        }else{
            ListFragment lf = new ListFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    datas = getMusicInfo();
                    ListFragment lf = new ListFragment();
                }
                break;
        }
    }

    public ArrayList<MusicData> getMusicInfo(){
        ArrayList<MusicData> datas = new ArrayList<>();

        String projections[] = {
                MediaStore.Audio.Media._ID,       // 노래아이디
                MediaStore.Audio.Media.ALBUM_ID,  // 앨범아이디
                MediaStore.Audio.Media.TITLE,     // 제목
                MediaStore.Audio.Media.ARTIST     // 가수
        };

        //getContentResolver().query(주소, 검색해올컬럼명들, 조건절, 조건절에매핑되는값, 정렬);
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projections, null, null, null);
        /*
        - uri        : content://스키마 형태로 정해져 있는 곳의 데이터를 가져온다
        - projection : 가져올 컬럼 이름들의 배열. null 을 입력하면 모든값을 가져온다
        - selection : 조건절(where)에 해당하는 내용
        - selectionArgs : 조건절이 preparedstatement 형태일 때 ? 에 매핑되는 값의 배열
        - sort order    : 정렬 조건
         */

        if(cursor != null){
            while(cursor.moveToNext()){
                MusicData data = new MusicData();
                // 데이터에 가수이름을 입력
                // 1. 가수 이름 컬럼의 순서(index)를 가져온다
                int idx = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                // 2. 해당 index를 가진 컬럼의 실제값을 가져온다
                data.artist = cursor.getString(idx);

                idx = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                data.title = cursor.getString(idx);

                idx = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                data.albumId = cursor.getString(idx);

                idx = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                data.musicId = cursor.getString(idx);

                datas.add(data);
            }
        }
        cursor.close();
        return datas;
    }

    @Override
    public void onFragmentInteraction(int position) {
        Log.i("MusicData", "----------------mainactivity도착");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        PagerFragment pf = new PagerFragment();
        tr.replace(R.id.fragment2, pf);
        tr.commit();
        pf.updateInfo(position);
    }
}
