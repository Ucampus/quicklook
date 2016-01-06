package cl.uchile.ing.adi.quicklooklib.fragments;

import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Created by dudu on 05-01-2016.
 */
public class FileItem {

    private File file;
    private String path;
    private String name;
    private String type;
    private int image;

    public FileItem(String path) {
        this(new File(path));
    }

    public FileItem(File file) {
        this.file = file;
        this.populateData();
    }

    public static ArrayList<FileItem> getElements(String path) {
        if (new File(path).isDirectory()) {
            File folder = new File(path);
            File[] elements = folder.listFiles();
            ArrayList<FileItem> files = new ArrayList<>();
            for (File elem : elements) {
                files.add(new FileItem(elem));
            }
            return files;
        }
        return null;
    }

    public static ArrayList<ZipEntry> peekZip(String path) {
        try {
            ZipFile zip = new ZipFile(path);
            Enumeration<? extends ZipEntry> elements = zip.entries();
            ArrayList<ZipEntry> files = new ArrayList<>();
            while (elements.hasMoreElements()) {
                ZipEntry elem = elements.nextElement();
                files.add(elem);
            }
            return files;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getImage(String path) {
        File f = new File(path);
        if (f.isDirectory()) {
            return R.drawable.folder;
        } else {
            if (f.getPath().endsWith(".mp3")) {
                return R.drawable.sound;
            } else if (f.getPath().endsWith(".pdf")) {
                return R.drawable.pdf;
            } else if (f.getPath().endsWith(".docx")) {
                return R.drawable.word;
            } else if (f.getPath().endsWith(".png") || f.getPath().endsWith(".jpg")) {
                return R.drawable.image;
            } else {
                return R.drawable.document;
            }
        }

    }

    private void populateData() {
        this.name = file.getName();
        this.path = file.getPath();
        this.image = getImage(this.path);
        this.type = getMimeType(path);
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public File getFile() {
        return this.file;
    }

    public String getPath() {
        return this.path;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public int getImage() {
        return this.image;
    }

}