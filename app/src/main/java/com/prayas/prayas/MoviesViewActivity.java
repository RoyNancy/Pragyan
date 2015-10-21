package com.prayas.prayas;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class MoviesViewActivity extends AppCompatActivity  {

    private Cursor mCursor;
    private String mWhereClause;
    private String mSortOrder;

    private Activity activity;

    private ListView moviesListView;
    private ArrayList<MovieDetail> movieDetailArrayList = new ArrayList<>();

    private  MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_view);

        getSupportActionBar().setTitle("Movies");
        Drawable drawable;
        // int decode = Integer.decode("3F51B5");
        drawable = new ColorDrawable(0xFF3F51B5);
        getSupportActionBar().setBackgroundDrawable(drawable);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        activity = this;

        MakeCursor();

    }


    private void MakeCursor() {
        String[] cols = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.DURATION
        };
        ContentResolver resolver = getContentResolver();
        if (resolver == null) {
            System.out.println("resolver = null");
        } else {
            String selection=MediaStore.Video.Media.DATA +" like?";
            String[] selectionArgs=new String[]{"%PrayasMovies%"};
            mSortOrder = MediaStore.Video.Media.TITLE + " COLLATE UNICODE";
            mWhereClause = MediaStore.Video.Media.TITLE + " != ''";
            mCursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    cols, selection , selectionArgs, mSortOrder);


            if (mCursor == null) {
                //MusicUtils.displayDatabaseError(this);
                //TODO: show error
                return;
            }

            if (mCursor.getCount() > 0) {
                getSupportActionBar().setTitle("Movies");// setTitle(R.string.videos_title);
            } else {
                getSupportActionBar().setTitle("Movies Not Found");// setTitle(R.string.no_videos_title);
            }

            String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA,
                    MediaStore.Video.Thumbnails.VIDEO_ID };

            //TODO: parse cursors
            if(mCursor.moveToFirst()){
                do {

                    MovieDetail movieInfo = new MovieDetail();

                    int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));

                    MediaStore.Video.Thumbnails.getThumbnail(activity.getContentResolver(),
                            id, MediaStore.Video.Thumbnails.MICRO_KIND, null);

                    Cursor thumbCursor = resolver.query( MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                            thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                    + " = " + id, null, null);

                    Log.d("thumb cursor:", thumbCursor.getCount()+"count");

                    if (thumbCursor.moveToFirst()) {
                        movieInfo.thumbPath = thumbCursor.getString(thumbCursor
                                .getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                        Log.v("", movieInfo.thumbPath);
                    }

                    movieInfo.filePath = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    movieInfo.movieTitle = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    movieInfo.movieArtist = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                    movieInfo.mimeType = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    movieInfo.movieDuration = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                  //  movieInfo.mimeType = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));

                    movieDetailArrayList.add(movieInfo);

                }while (mCursor.moveToNext());
            }

            //Set Adapter

            moviesListView = (ListView) findViewById(R.id.movieListView);
            moviesAdapter = new MoviesAdapter(activity, movieDetailArrayList);

            moviesListView.setAdapter(moviesAdapter);
            moviesListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO: TBD

                    MovieDetail viewHolder = (MovieDetail) view.getTag(R.id.folder_holder);

                   // int fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                   // int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE);
                    //String videoFilePath = cursor.getString(fileColumn);
                   // String mimeType = cursor.getString(mimeColumn);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                    File newFile = new File(viewHolder.filePath);
                    intent.setDataAndType(Uri.fromFile(newFile), viewHolder.mimeType);
                    startActivity(intent);

                }
            });
        }

    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movies_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}