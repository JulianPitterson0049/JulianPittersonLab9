// Julian Pitterson - N01680049
package julian.pitterson.n01680049.jp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PitFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private EditText pitFileNameInput, pitFileContentsInput;
    private LinearLayout pitFileListContainer;
    private boolean isPersistent = true;
    private ArrayList<String> fileNames = new ArrayList<>();

    public PitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PitFragment newInstance(String param1, String param2) {
        PitFragment fragment = new PitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pit, container, false);

        pitFileNameInput = view.findViewById(R.id.pitFileNameInput);
        pitFileContentsInput = view.findViewById(R.id.pitFileContentsInput);
        pitFileListContainer = view.findViewById(R.id.pitFileListContainer);

        Button pitPersistentBtn = view.findViewById(R.id.pitPersistentBtn);
        Button pitCreateBtn = view.findViewById(R.id.pitCreateBtn);
        Button pitDeleteBtn = view.findViewById(R.id.pitDeleteBtn);
        Button pitWriteBtn = view.findViewById(R.id.pitWriteBtn);
        Button pitReadBtn = view.findViewById(R.id.pitReadBtn);

        // Toggle persistent vs cache
        pitPersistentBtn.setOnClickListener(v -> {
            isPersistent = !isPersistent;
            pitPersistentBtn.setText(isPersistent ?
                    getString(R.string.btn_persistent) : getString(R.string.btn_cache));
        });

        // Load existing files on startup
        loadExistingFiles();

        pitCreateBtn.setOnClickListener(v -> {
            if (isFileNameMissing(view)) return;
            createFile();
        });

        pitDeleteBtn.setOnClickListener(v -> {
            if (isFileNameMissing(view)) return;
            deleteFile(view);
        });

        pitWriteBtn.setOnClickListener(v -> {
            if (isFileNameMissing(view)) return;
            String content = pitFileContentsInput.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.toast_content_missing),
                        Toast.LENGTH_LONG).show();
                return;
            }
            writeFile(content);
        });

        pitReadBtn.setOnClickListener(v -> {
            if (isFileNameMissing(view)) return;
            readFile();
        });

        return view;
    }

    // Returns true if file name is missing and shows snackbar
    private boolean isFileNameMissing(View view) {
        String fileName = pitFileNameInput.getText().toString().trim();
        if (fileName.isEmpty()) {
            Snackbar.make(view, getString(R.string.snack_file_name_missing),
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.dismiss_snack, v -> {})
                    .show();
            return true;
        }
        return false;
    }

    private File getFile() {
        String fileName = pitFileNameInput.getText().toString().trim();
        if (isPersistent) {
            return new File(requireContext().getFilesDir(), fileName);
        } else {
            return new File(requireContext().getCacheDir(), fileName);
        }
    }

    private void createFile() {
        try {
            File file = getFile();
            if (file.createNewFile()) {
                // Enforce max 3 files
                if (fileNames.size() >= 3) {
                    // Delete oldest file
                    String oldest = fileNames.remove(0);
                    File oldFile = isPersistent ?
                            new File(requireContext().getFilesDir(), oldest) :
                            new File(requireContext().getCacheDir(), oldest);
                    oldFile.delete();
                }
                String fileName = pitFileNameInput.getText().toString().trim();
                fileNames.add(fileName);
                refreshFileList();
                Toast.makeText(getContext(), getString(R.string.file_created) + fileName,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.file_already_exists, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(View view) {
        String fileName = pitFileNameInput.getText().toString().trim();
        File file = getFile();
        if (file.exists()) {
            file.delete();
            fileNames.remove(fileName);
            refreshFileList();
            Toast.makeText(getContext(), getString(R.string.file_deleted) + fileName,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.file_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    private void writeFile(String content) {
        try {
            File file = getFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            pitFileContentsInput.setText("");
            Toast.makeText(getContext(), R.string.file_written, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFile() {
        try {
            File file = getFile();
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            pitFileContentsInput.setText(sb.toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.error_reading_file, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadExistingFiles() {
        fileNames.clear();
        // Check persistent files
        File filesDir = requireContext().getFilesDir();
        if (filesDir.exists()) {
            for (File f : filesDir.listFiles() != null ? filesDir.listFiles() : new File[0]) {
                if (fileNames.size() < 3) fileNames.add(f.getName());
            }
        }
        refreshFileList();
    }

    private void refreshFileList() {
        pitFileListContainer.removeAllViews();
        for (String name : fileNames) {
            TextView tv = new TextView(getContext());
            tv.setText(name);
            tv.setTextSize(20);
            tv.setTypeface(null, Typeface.BOLD_ITALIC);
            tv.setTextColor(Color.parseColor("#6200EE"));
            pitFileListContainer.addView(tv);
        }
    }
}
