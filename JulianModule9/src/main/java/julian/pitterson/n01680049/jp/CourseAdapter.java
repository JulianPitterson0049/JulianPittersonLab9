// Julian Pitterson - N01680049
package julian.pitterson.n01680049.jp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private ArrayList<Course> courseList;

    public CourseAdapter(ArrayList<Course> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.julCourseName.setText(course.getName());
        holder.julCourseDesc.setText(course.getDescription());
    }

    @Override
    public int getItemCount() { return courseList.size(); }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView julCourseName, julCourseDesc;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            julCourseName = itemView.findViewById(R.id.julCourseName);
            julCourseDesc = itemView.findViewById(R.id.julCourseDesc);
        }
    }
}