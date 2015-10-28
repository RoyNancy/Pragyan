package com.prayas.prayas;

import android.content.Context;
import android.content.Intent;

import android.content.res.Configuration;

import android.os.Bundle;
import android.os.Environment;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.GridView;
import android.widget.ListView;


import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import  com.itextpdf.*;
import com.itextpdf.xmp.XMPException;
import com.itextpdf.xmp.XMPMeta;
import com.itextpdf.xmp.XMPMetaFactory;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFParser;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private ArrayList<BookDetail> bookDetailArrayList = new ArrayList<>();

    private BookGridAdapter bookGridAdapter;
    private GridView bookGridView;
    private Context mContext;

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private String[] mStoreTitles;
    private String[] mAppTitles;
    private String[] mActivities;
    private  String[] mSectionTitles;
    private String[] mMenuItem;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Toolbar toolbar;
    private DrawerAdapter mAdapter;

    private HashMap<String, String> drawerItems = new HashMap<>();
   // private static final String SECTION = "SECTION";
   // private static final String ITEM  = "ITEM";

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Books");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar_layout);
        mTitle = "Books";

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        mActivities = new String[]{"My Store", "My Order"};
        mStoreTitles = new String[]{"Books", "Movies", "Games", "Food" };
        mAppTitles = new String[]{"About", "Send Feedback", "Contact" };
        mSectionTitles = new String []{"ACTIVITIES", "EXPLORE STORE", "PRAYAS"};

        mMenuItem = new String []{"My Store", "My Order","Books", "Movies", "Games", "Food","About", "Send Feedback", "Contact" };
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mAdapter = new DrawerAdapter(this);
        for (int i = 0; i < mSectionTitles.length; i++) {

            mAdapter.addSectionHeaderItem(mSectionTitles[i]);
            //drawerItems.put(mSectionTitles[i], SECTION);
            switch (i){
                case 0:
                    for (int item = 0; item < mActivities.length; item++) {
                        Log.d("item menu", mActivities[item]);
                     mAdapter.addItem(mActivities[item]);
                       // drawerItems.put(mActivities[item], mActivities[item]);
                    }
                    break;
                case 1:
                    for (int item = 0; item < mStoreTitles.length; item++) {
                     mAdapter.addItem(mStoreTitles[item]);
                        //drawerItems.put(mStoreTitles[item], mStoreTitles[item]);
                    }
                    break;
                case 2:
                    for (int item = 0; item < mAppTitles.length; item++) {
                     mAdapter.addItem(mAppTitles[item]);
                       // drawerItems.put(mAppTitles[item], mAppTitles[item]);
                    }
                    break;
                    default:
                        break;
            }

        }

        // Set the adapter for the list view
       // mAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, mStoreTitles);
        Log.d("log", "test" + mAdapter + "/////" + mDrawerList);
        mDrawerList.setAdapter(mAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getDelegate().getSupportActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getDelegate().getSupportActionBar().setTitle(mTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getDelegate().getSupportActionBar().setHomeButtonEnabled(true);

        bookGridView = (GridView)findViewById(R.id.grid);

        loadBookGridView();
    }


    public  void  loadBookGridView(){

        getBooksFromDevice();

        bookGridAdapter = new BookGridAdapter(mContext,bookDetailArrayList);
        bookGridView.setAdapter(bookGridAdapter);
        bookGridView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BookDetail viewHolder = (BookDetail) view.getTag(R.id.folder_holder);
               // File directory = new File(viewHolder.bookFilePath.getFile());
                showBook(viewHolder);

            }
        });

    }

    public void getBooksFromDevice(){

        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PrayasBook");

        if(!directory.exists()) {
            directory.mkdirs();
            //createFolder(m_applicationDir + "/FunnyB/public/"+ publicFolderNames[i], publicFolderNames[i]);
        }
        this.walkdir(directory);

    }

    public void walkdir(File dir) {
        String pdfPattern = ".pdf";
        String epubPattern = ".epub";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkdir(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)){
                        //Do what ever u want
                        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PrayasBook/" + listFile[i].getName() );
                        Log.d("file name", listFile[i].getName());
                        File sampleDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PrayasBookSample/" + listFile[i].getName() );

                        try {

                            URL url = directory.toURI().toURL();
                            Log.d("url Path", url.getPath());
                            getPDFMetaData(url, directory, sampleDirectory);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }


                    }else if(listFile[i].getName().endsWith(epubPattern)){
                        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PrayasBook/" + listFile[i].getName() );

                       // getBookMetaDataFromPdf(directory.getAbsolutePath(), directory);
                        Log.d("file name", listFile[i].getName());
                    }
                }
            }
        }
    }

    /*public void getBookMetaDataFromPdf(String bookFile, File directory){


        try (FileInputStream fileInputStream = new FileInputStream(bookFile)) {

            Book book = (new EpubReader()).readEpub(fileInputStream);
            Log.d("book", book.getMetadata().getFormat());
            Log.d("book", String.valueOf(book.getMetadata().getTitles()));
            Log.d("book", String.valueOf(book.getMetadata().getAuthors()));
            BookDetail bookD = new BookDetail();
            bookD.bookName = String.valueOf(book.getMetadata().getTitles());//"Book Name";
            bookD.authorName = String.valueOf(book.getMetadata().getAuthors());//"Author";
            bookD.bookDescription = "Deatialed Book";
            bookD.bookIcon = R.drawable.comic;

            //File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PrayasBook/" + listFile[i].getName() );

            bookD.bookFilePath = directory.getAbsolutePath(); //listFile[i].getName();
            bookDetailArrayList.add(bookD);
        }catch (IOException e){
            Log.d("book", "exception");
        }

    }*/

    public void getPDFMetaData(URL bookFile, File directory, File sampleDirectory){


        //storage/emulated/0/PrayasBook/Swift%20Quick%20Syntax%20Reference.pdf
        try {
            Log.d("url name", bookFile.toString()+ "::file::"+ directory.toString());
            PdfReader reader = new PdfReader(bookFile);

            RandomAccessFile raf = new RandomAccessFile(directory, "r");

            FileChannel channel = raf.getChannel();

           net.sf.andpdf.nio.ByteBuffer buf = net.sf.andpdf.nio.ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
            PDFFile pdffile = new PDFFile(buf);

            // draw the first page to an image
            PDFPage page = pdffile.getPage(0);

            Log.d("metadata", "check"+page.getBBox());
            //get the width and height for the doc at the default zoom
            Rectangle rect = new Rectangle(0,0,
                    (int)page.getBBox().width(),
                    (int)page.getBBox().height());

            //generate the image
//            Bitmap img = page.getImage((int) rect.width, (int) rect.height, null, true, true);

            Log.d("metadata", "check2");
            if (reader.getMetadata() != null){
                String st = new String(reader.getMetadata());
                Log.d("metadata:", st);
            }
            //
            BookDetail bookD = new BookDetail();
            Map info = reader.getInfo();
            for (Iterator i = info.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                String value = (String) info.get(key);
                if(key.equals("Author")){
                    bookD.authorName = value;
                }else if(key.equals("Title")){
                    bookD.bookName = value;
                }

               System.out.println(key + ": " + value);
            }

           // Log.d("reader", reader.getMetadata().toString()+"hj"+st);
            //Log.d("catalog", reader.getCatalog() + "jhf");
          //  System.out.println("reader.getPageResources(0)"+reader.getPageResources(0));

           // bookD.bookName = "Book Name";
            //bookD.authorName = "Vijay Rastogi";
            bookD.bookDescription = "Comic Book";
            bookD.bookIcon = R.drawable.comic;
            bookD.bookPrice = "5 Rs";
            bookD.bookISBN = "ISBN Number";
            bookD.bookPublishedDate = "22/3/15";
            bookD.bookPublisher = "Vipul and Co.";
            //File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PrayasBook/" + listFile[i].getName() );

            URL url = directory.toURI().toURL();
            bookD.bookFilePath = url; //directory.getAbsolutePath(); //listFile[i].getName();
            bookD.bookDiretory = directory.getAbsolutePath();

            URL urlSample = sampleDirectory.toURI().toURL();
            bookD.bookSampeFilePath = urlSample;
            bookD.bookSampleDirectory = sampleDirectory.getAbsolutePath();

            bookDetailArrayList.add(bookD);
           // reader.getMetadata();
        } catch (IOException e) {
            Log.d("url exception", bookFile.toString());
            e.printStackTrace();
        }

    }

    public void showBook(BookDetail bookDetail){
        Intent intent = new Intent(MainActivity.this,BookDetailActivity.class);
        Bundle information = new Bundle();

        information.putSerializable("BookDetail", bookDetail);

        intent.putExtras(information);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }else {

            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position, String menuItem) {
       // Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
      //  setTitle(mMenuItem[position]);
        mDrawerLayout.closeDrawer(mDrawerList);

        Log.d("position", position + "");
        Intent intent;
        Bundle information = new Bundle();
        switch (menuItem) {
            case "My Store":
                intent = new Intent(MainActivity.this,MyStoresActivity.class);

                intent.putExtras(information);
                startActivity(intent);
                break;
            case "My Order":
                intent = new Intent(MainActivity.this,MyOrdersActivity.class);

                intent.putExtras(information);
                startActivity(intent);
                break;
            case "Books":

                break;
            case "Movies":
                intent  = new Intent(MainActivity.this,MoviesViewActivity.class);

                intent.putExtras(information);
                startActivity(intent);
                break;
            case "Games":
                intent = new Intent(MainActivity.this,GamesViewActivity.class);

                intent.putExtras(information);
                startActivity(intent);
                break;
            case "Food":
                String packageName = "com.cymaxtec.restomenu";
                intent = getPackageManager().getLaunchIntentForPackage(packageName);
                if(intent != null) {
                    startActivity(intent);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
       // TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        //toolbarTitle.set
        getDelegate().getSupportActionBar().setTitle(mTitle);
       // getSupportActionBar().setTitle(mTitle);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            String item = (String) view.getTag(R.id.folder_holder);
            selectItem(position, item);
        }
    }


}
