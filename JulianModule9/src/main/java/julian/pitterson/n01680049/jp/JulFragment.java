// Julian Pitterson - N01680049
package julian.pitterson.n01680049.jp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JulFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JulFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String PREFS_NAME = "CoursePrefs";
    private static final String COURSES_KEY = "courses";

    private ArrayList<Course> courseList;
    private CourseAdapter adapter;
    private EditText julCourseNameInput, julCourseDescInput;
    private SharedPreferences prefs;
    private Gson gson;

    public JulFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JulFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JulFragment newInstance(String param1, String param2) {
        JulFragment fragment = new JulFragment();
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
        View view = inflater.inflate(R.layout.fragment_jul, container, false);

        prefs = requireActivity().getSharedPreferences(PREFS_NAME, requireActivity().MODE_PRIVATE);
        gson = new Gson();

        julCourseNameInput = view.findViewById(R.id.julCourseNameInput);
        julCourseDescInput = view.findViewById(R.id.julCourseDescInput);
        Button julSaveBtn = view.findViewById(R.id.julSaveBtn);
        Button julAddBtn = view.findViewById(R.id.julAddBtn);
        Button julDeleteBtn = view.findViewById(R.id.julDeleteBtn);
        RecyclerView julRecyclerView = view.findViewById(R.id.julRecyclerView);

        // Load saved courses
        courseList = loadCourses();
        adapter = new CourseAdapter(courseList);
        julRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        julRecyclerView.setAdapter(adapter);

        // Add button - adds to list and updates view
        julAddBtn.setOnClickListener(v -> {
            String name = julCourseNameInput.getText().toString().trim();
            String desc = julCourseDescInput.getText().toString().trim();
            if (!name.isEmpty() && !desc.isEmpty()) {
                courseList.add(new Course(name, desc));
                adapter.notifyItemInserted(courseList.size() - 1);
                julCourseNameInput.setText("");
                julCourseDescInput.setText("");
            }
        });

        // Save button - saves current list to SharedPrefs
        julSaveBtn.setOnClickListener(v -> {
            saveCourses();
            Toast.makeText(getContext(), getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
        });

        // Delete button - clears all data
        julDeleteBtn.setOnClickListener(v -> {
            if (courseList.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.toast_no_data), Toast.LENGTH_SHORT).show();
            } else {
                courseList.clear();
                adapter.notifyDataSetChanged();
                prefs.edit().remove(COURSES_KEY).apply();
            }
        });

        return view;
    }
    private void saveCourses() {
        String json = gson.toJson(courseList);
        prefs.edit().putString(COURSES_KEY, json).apply();
    }

    private ArrayList<Course> loadCourses() {
        String json = prefs.getString(COURSES_KEY, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<Course>>() {}.getType();
        return gson.fromJson(json, type);
    }
}