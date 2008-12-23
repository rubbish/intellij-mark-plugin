package intellij.mark;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.actionSystem.ActionManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.HashMap;


public class MarkManager implements ApplicationComponent {
    public Map<Editor, MarkCaretListener> getEditorMarks() {
        return editorMarks;
    }

    Map<Editor, MarkCaretListener> editorMarks;

    public MarkManager() {
        editorMarks = new HashMap<Editor, MarkCaretListener>();
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "MarkManager";
    }

    public void setMark(Editor editor) {
        CaretModel caretModel = editor.getCaretModel();

        MarkCaretListener markCaretListener = editorMarks.get(editor);
        if (markCaretListener != null) {
            caretModel.removeCaretListener(markCaretListener);
        }

        SelectionModel selectionModel = editor.getSelectionModel();
        MarkCaretListener listener = new MarkCaretListener(caretModel);
        caretModel.addCaretListener(listener);
        editorMarks.put(editor, listener);
    }


    public SelectionModel setSelectionToMarkRange(Editor editor) {
        SelectionModel selectionModel = editor.getSelectionModel();
        MarkCaretListener markCaretListener = editorMarks.get(editor);
        if (markCaretListener != null) {
            setSelection(selectionModel, markCaretListener);
            editor.getCaretModel().removeCaretListener(markCaretListener);
            editorMarks.remove(editor);
        }
        return selectionModel;
    }

    private void setSelection(SelectionModel selectionModel, MarkCaretListener markCaretListener) {
        selectionModel.setSelection(markCaretListener.getOriginalOffset(), markCaretListener.getCurrentOffset());
    }

    public void exchangePointAndMark(Editor editor) {
        MarkCaretListener markCaretListener = editorMarks.get(editor);

        markCaretListener.exchange();

        CaretModel model = editor.getCaretModel();
        model.moveToOffset(markCaretListener.getCurrentOffset());

        setSelection(editor.getSelectionModel(), markCaretListener);
    }
}
