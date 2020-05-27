package com.vinberts.gifvault.controllers.folders;

import com.vinberts.gifvault.data.GifFolder;
import com.vinberts.gifvault.database.DatabaseHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.vinberts.gifvault.utils.AppConstants.ROOT_TREE_FOLDER_NAME;
import static com.vinberts.gifvault.utils.AppConstants.ROOT_TREE_ID;

/**
 *
 */
public class ManageFoldersController implements Initializable {

    @FXML
    private VBox manageFolderContainer;

    private TreeView folderTreeView;

    private TreeItem<GifFolder> rootNode;

    private Image folderIcon = new Image(ManageFoldersController.class.getResourceAsStream("/folder.png"));
    private Node rootIcon =
            new ImageView(new Image(ManageFoldersController.class.getResourceAsStream("/folders.png")));

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        GifFolder dummyRoot = new GifFolder();
        dummyRoot.setName(ROOT_TREE_FOLDER_NAME);
        dummyRoot.setId(ROOT_TREE_ID);
        dummyRoot.setCreatedAt(new Date());

        this.rootNode = new TreeItem<>(dummyRoot, rootIcon);
        rootNode.setExpanded(true);

        List<GifFolder> gifFolderList = DatabaseHelper.getListOfFolders(50, 0, true);

        for (GifFolder folder: gifFolderList) {
            TreeItem<GifFolder> folderNode = new TreeItem<>(
                    folder,
                    new ImageView(folderIcon)
            );

            rootNode.getChildren().add(folderNode);
        }

        folderTreeView = new TreeView<>(rootNode);
        folderTreeView.setEditable(true);
        folderTreeView.setCellFactory((p) ->
                new TextFieldTreeCellImpl());

        manageFolderContainer.getChildren().add(folderTreeView);


    }
}
