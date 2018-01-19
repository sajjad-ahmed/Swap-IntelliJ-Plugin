import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * ------------------------------------------------------------------------------------------------*
 * File name: MainAction.java
 * Project name: Swap
 * Author: SAJJAD AHMED NILOY
 * Created on: 12:03 AM 12-Jan-18
 * License: MIT License
 * <p>
 * Copyright (c) 2018 sajjad ahmed niloy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ------------------------------------------------------------------------------------------------*
 **/

public class MainAction extends AnAction
{
    @Override
    public void actionPerformed(AnActionEvent e)
    {
        Editor editor = getEditor(e);
        String selectedText = editor.getSelectionModel().getSelectedText();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        String pasteText;
        try
        {
            pasteText = (String) clipboard.getData(DataFlavor.stringFlavor);
            StringSelection stringSelection = new StringSelection(selectedText);
            clipboard.setContents(stringSelection, stringSelection);
        } catch (UnsupportedFlavorException e1)
        {
            Messages.showInfoMessage(e.getProject(), "The clipboard doesn't contain any string.", "Can't Swap");
            return;
        } catch (IOException e1)
        {
            return;
        }
        Document editorDocument = editor.getDocument();
        String editorText = editorDocument.getText();
        String finalPasteText = pasteText;
        new WriteCommandAction.Simple(e.getProject())
        {
            @Override
            protected void run() throws Throwable
            {
                int start = editorText.indexOf(selectedText);
                int end = start + selectedText.length();
                editorDocument.replaceString(start, end, finalPasteText);
            }
        }.execute();
    }

    public void update(AnActionEvent e)
    {
        Editor editor = getEditor(e);
        if (editor == null)
            e.getPresentation().setEnabled(false);
    }

    private Editor getEditor(AnActionEvent e)
    {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor.getSelectionModel().getSelectedText() == null)
        {
            return null;
        }
        if (psiFile == null || editor == null)
        {
            return null;
        }
        return editor;
    }
}