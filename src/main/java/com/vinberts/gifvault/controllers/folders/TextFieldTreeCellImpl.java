package com.vinberts.gifvault.controllers.folders;

import com.vinberts.gifvault.data.GifFolder;
import com.vinberts.gifvault.data.GifVault;
import com.vinberts.gifvault.database.DatabaseHelper;
import com.vinberts.gifvault.utils.AlertMaker;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.vinberts.gifvault.utils.AppConstants.ROOT_TREE_ID;
import static com.vinberts.gifvault.utils.AppConstants.UNCATEGORIZED;

/**
 *
 */
@Slf4j
public class TextFieldTreeCellImpl extends TreeCell<GifFolder> {
    private TextField textField;
    private final ContextMenu addFolderMenu = new ContextMenu();
    private final ContextMenu removeFolderMenu = new ContextMenu();
    private final Image folderIcon = new Image(TextFieldTreeCellImpl.class.getResourceAsStream("/folder.png"));

    public TextFieldTreeCellImpl() {
        MenuItem addMenuItem = new MenuItem("Add Folder");
        addFolderMenu.getItems().add(addMenuItem);
        addMenuItem.setOnAction((ActionEvent t) -> {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Folder name");
            dialog.setHeaderText("Enter a name for your folder.");
            Optional<String> userInput = dialog.showAndWait();
            if (userInput.isPresent()) {
                GifFolder newFolder = new GifFolder();
                newFolder.setCreatedAt(new Date());
                newFolder.setName(userInput.get());
                newFolder.setId(UUID.randomUUID().toString());
                ArrayList<GifVault> gifVaultList = new ArrayList();
                newFolder.setGifVaultEntries(gifVaultList);
                boolean inserted = DatabaseHelper.insertNewGifFolder(newFolder);
                if (inserted) {
                    TreeItem<GifFolder> treeItem =
                            new TreeItem<>(newFolder, new ImageView(folderIcon));
                    getTreeItem().getChildren().add(treeItem);
                } else {
                    AlertMaker.showErrorMessage("Error", "The folder title you picked has already been created.");
                }
            } else {
                log.info("user did not enter a folder name.");
            }
        });
        MenuItem removeMenuItem = new MenuItem("Remove Folder");
        removeFolderMenu.getItems().add(removeMenuItem);
        removeMenuItem.setOnAction((ActionEvent t) -> {
            GifFolder currentFolder = this.getItem();
            if (currentFolder.getName().equals(UNCATEGORIZED)) {
                AlertMaker.showSimpleAlert("Sorry", "You cannot remove the Uncategorized folder.");
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Are you sure?");
                String s = "Confirm to remove this folder and its contents will be placed in Uncategorized.";
                alert.setContentText(s);
                Optional<ButtonType> result = alert.showAndWait();
                if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                    log.info("attempting to remove folder! " + currentFolder.getName());
                    boolean suc = DatabaseHelper.removeGifFolderAndTransferItsChildren(currentFolder);
                    if (suc) {
                        getTreeItem().getParent().getChildren().remove(this.getTreeItem());
                    } else {
                        AlertMaker.showSimpleAlert("Uh-oh", "Something went wrong trying to remove the folder.");
                    }
                } else {
                    log.debug("user canceled");
                }
            }
        });
    }

    @Override
    public void startEdit() {
        if (!getTreeItem().getValue().getId().equals(ROOT_TREE_ID)
                && !getTreeItem().getValue().getName().equals(UNCATEGORIZED)) {
            super.startEdit();

            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText(getItem().getName());
        setGraphic(getTreeItem().getGraphic());
    }

    @Override
    public void updateItem(GifFolder item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
                if (getTreeItem().getValue().getId().equals(ROOT_TREE_ID)) {
                    setContextMenu(addFolderMenu);
                }
                // else add other context menu options
                else if (Objects.nonNull(getTreeItem().getParent())) {
                    setContextMenu(removeFolderMenu);
                }
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased((KeyEvent t) -> {
            if (t.getCode() == KeyCode.ENTER) {
                log.info("Committing folder edit change: " + textField.getText());
                GifFolder updatedFolder = this.getItem();
                updatedFolder.setName(textField.getText());
                boolean updated = DatabaseHelper.updateGifFolder(updatedFolder);
                if (updated) {
                    commitEdit(updatedFolder);
                } else {
                    AlertMaker.showErrorMessage("Error", "The folder title you picked has already been created.");
                    cancelEdit();
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });

    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }


}
