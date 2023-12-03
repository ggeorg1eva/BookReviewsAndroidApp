package com.example.bookreviewsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bookreviewsapp.entity.view.BookView;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<BookView> {
    private Context context;
    private int resource;


    public CustomArrayAdapter(@NonNull Context context, int resource, @NonNull List<BookView> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    /**
     * This method makes the connection between the BookView instance and one line in the ListView from the UI.
     * In it all columns are set and the logic for the Edit and Delete are implemented.
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return the view, which will be one line in the ListView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookView bookView = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource,
                    parent, false);
        }
        TextView title = convertView.findViewById(R.id.title);
        TextView author = convertView.findViewById(R.id.author);
        TextView isRead = convertView.findViewById(R.id.isRead);
        Button editBtn = convertView.findViewById(R.id.buttonEdit);
        Button deleteBtn = convertView.findViewById(R.id.buttonDelete);

        if (position == 0) {
            title.setText("Title");
            author.setText("Author");
            isRead.setText("Have I read it");
            editBtn.setVisibility(View.INVISIBLE);
            deleteBtn.setVisibility(View.INVISIBLE);

        } else {
            title.setText(bookView.getTitle());
            author.setText(bookView.getAuthor());
            isRead.setText(bookView.getIsRead());
            editBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
            convertView.setFocusable(true);
            convertView.setClickable(true);

            editBtn.setOnClickListener(action -> {
                BookView bookToEdit = getItem(position);
                showEditForm(bookToEdit);
            });

            deleteBtn.setOnClickListener(v -> {
                BookView bookToDelete = getItem(position);
                showDeleteConfirmation(bookToDelete);
                
            });

            convertView.setOnClickListener(v -> {
                showInformationAboutBook(bookView);
            });
        }

        return convertView;
    }

    /**
     * Shows full information about the book that has been clicked
     * @param book
     */
    private void showInformationAboutBook(BookView book) {
        new AlertDialog.Builder(getContext(), R.style.AlertDialogInfo)
                .setTitle("Book Information")
                .setMessage("Title: " + book.getTitle() +
                        "\nAuthor: " + book.getAuthor() +
                        "\nYear of publish: " + book.getYearOfPublish() +
                        "\nHave I read it: " + book.getIsRead())
                .setNeutralButton("Close", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    /**
     * Updates the book with the assigned values from the user after input validation.
     * @param bookToEdit
     */
    private void showEditForm(BookView bookToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogInfo);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.edit_book, null);

        EditText editTextTitle = dialogView.findViewById(R.id.editTitleText);
        EditText editTextAuthor = dialogView.findViewById(R.id.editAuthorText);
        EditText editTextYear = dialogView.findViewById(R.id.editYearOfPublishText);
        Spinner editIsReadSpinner = dialogView.findViewById(R.id.editIsReadSpinner);

        // Prepopulate fields with current book data
        editTextTitle.setText(bookToEdit.getTitle());
        editTextAuthor.setText(bookToEdit.getAuthor());
        editTextYear.setText(bookToEdit.getYearOfPublish());

        // Get the adapter associated with the Spinner
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) editIsReadSpinner.getAdapter();

        // Find the position of the desired value in the adapter
        int position = adapter.getPosition(bookToEdit.getIsRead());
        editIsReadSpinner.setSelection(position);


        builder.setView(dialogView)
                .setTitle("Edit book")
                .setPositiveButton("Save changes", (dialog, which) -> {
                    // Get values from dialog
                    String title = editTextTitle.getText().toString();
                    String author = editTextAuthor.getText().toString();
                    String year = editTextYear.getText().toString();
                    String isRead = editIsReadSpinner.getSelectedItem().toString();

                    bookToEdit.setTitle(title);
                    bookToEdit.setAuthor(author);
                    bookToEdit.setYearOfPublish(year);
                    bookToEdit.setIsRead(isRead);

                    updateBookInDB(bookToEdit);
                    notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    /**
     * updates the book in the DB
     * @param bookToEdit
     */
    private void updateBookInDB(BookView bookToEdit) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        boolean isValid = dbHelper.validateInput(bookToEdit.getTitle(), bookToEdit.getAuthor(), bookToEdit.getYearOfPublish(), bookToEdit.getIsRead());

        if (isValid){
            dbHelper.update(bookToEdit);
        }
        dbHelper.close();
    }

    /**
     * Shows a popup which asks the user whether to delete the book or not. If they click Yes,
     * the bookview is deleted from the listview and the book from the DB (the latter happens in the method deleteBookFromDB(BookView book).
     * @param book - book to delete
     */
    private void showDeleteConfirmation(BookView book) {
        new AlertDialog.Builder(getContext(), R.style.AlertDialogWarn)
                .setTitle("Delete Book")
                .setMessage("Are you sure you want to delete this book (Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ")?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    remove(book);
                    notifyDataSetChanged();
                    deleteBookFromDB(book);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    /**
     * deletes the book from the DB
     * @param book
     */
    private void deleteBookFromDB(BookView book) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        if (book.getId() != null){
            dbHelper.delete(book.getId());
        }
        dbHelper.close();
    }

}
