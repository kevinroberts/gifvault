package com.vinberts.gifvault.database;

import com.trievosoftware.giphy4j.entity.giphy.GiphyData;
import com.vinberts.gifvault.data.GifFolder;
import com.vinberts.gifvault.data.GifVault;
import com.vinberts.gifvault.data.GiphyGif;
import com.vinberts.gifvault.utils.AppUtils;
import com.vinberts.gifvault.views.GiphyCell;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.vinberts.gifvault.utils.AppConstants.GIPHY_DATE_FORMAT;

/**
 *
 */
@Slf4j
public class DatabaseHelper {

    public static boolean insertNewGifFolder(GifFolder folder) {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(folder);
            transaction.commit();
            session.close();
            return true;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Exception occurred trying to commit new GIF folder", e);
        }
        return false;
    }

    public static boolean insertNewGifVaultEntry(GifVault gifVault) {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(gifVault);
            transaction.commit();
            session.close();
            return true;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Exception occurred trying to commit new GIF folder", e);
        }
        return false;
    }

    public static boolean insertNewGiphyEntry(GiphyCell giphyCell, GifVault vaultEntry) {
        Transaction transaction = null;
        GiphyData giphyData = giphyCell.getGiphyData();
        SimpleDateFormat format = new SimpleDateFormat(GIPHY_DATE_FORMAT);

        GiphyGif giphyGif = new GiphyGif();
        giphyGif.setId(giphyData.getId());
        giphyGif.setUrl(giphyData.getUrl());
        giphyGif.setBitlyUrl(giphyData.getBitlyUrl());
        giphyGif.setEmbedUrl(giphyData.getEmbedUrl());
        giphyGif.setFixedHeightMp4(giphyData.getImages().getFixedHeight().getMp4());
        if (StringUtils.isNotEmpty(giphyData.getTitle())) {
            giphyGif.setGiphyTitle(giphyData.getTitle());
        } else {
            giphyGif.setGiphyTitle("Gif By " + giphyData.getUsername());
        }
        giphyGif.setOriginalUrl(giphyData.getImages().getOriginal().getUrl());
        giphyGif.setOriginalMp4(giphyData.getImages().getOriginalMp4().getMp4());
        giphyGif.setFixedHeightUrl(giphyData.getImages().getFixedHeight().getUrl());
        giphyGif.setRating(giphyData.getRating());
        giphyGif.setSticker(giphyData.getUrl().contains("/stickers"));
        giphyGif.setGifVaultEntry(vaultEntry);
        vaultEntry.setGiphyGif(giphyGif);
        giphyGif.setUsername(giphyData.getUsername());
        try {
            giphyGif.setImportedAt(format.parse(giphyData.getImportDatetime()));
        } catch (ParseException e) {
            log.error("Could not parse date time from giphy data: " + giphyData.getImportDatetime());
            giphyGif.setImportedAt(new Date());
        }

        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(giphyGif);
            transaction.commit();
            session.close();
            return true;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Exception occurred trying to commit new GIF folder", e);
        }
        return false;
    }

    public static List<GifVault> getGifVaultsByFolder(int limit, int offset, GifFolder folder) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        String hql = "FROM GifVault V WHERE V.folder = :folder ORDER BY createdAt desc";
        Query query = session.createQuery(hql);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        query.setParameter("folder", folder);
        List<GifVault> results = query.list();
        return results;
    }

    public static Optional<GiphyGif> getGiphyGifById(String id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        IdentifierLoadAccess<GiphyGif> identifierLoadAccess = session.byId(GiphyGif.class);
        Optional<GiphyGif> gifOptional = identifierLoadAccess.loadOptional(id);
        session.close();
        return gifOptional;
    }

    public static String deleteGiphyGifById(String id) {
        Optional<GiphyGif> gifOptional = getGiphyGifById(id);
        if (gifOptional.isPresent()) {
            Transaction transaction = null;
            try {
                Session session = HibernateUtil.getSessionFactory().openSession();
                transaction = session.beginTransaction();
                String hql = "DELETE FROM GiphyGif G WHERE G.id = :id";
                Query query = session.createQuery(hql);
                query.setParameter("id", id);
                query.executeUpdate();
                transaction.commit();
                session.close();
                // remove associated files from gif vault / filesystem
                AppUtils.removeAssetsFromFileSystem(gifOptional.get().getGifVaultEntry());
                // return gif vault id for removal
                return gifOptional.get().getGifVaultEntry().getId();
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                log.error("Exception occurred trying to remove GIF", e);
                return "";
            }
        } else {
            return "";
        }
    }

    public static void deleteGifVaultById(String id) {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            String hql = "DELETE FROM GifVault G WHERE G.id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("id", id);
            query.executeUpdate();
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Exception occurred trying to remove GIF vault", e);
        }
    }

    public static GifFolder getUncategorizedFolder() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        String hql = "FROM GifFolder F WHERE F.name = :name";
        Query query = session.createQuery(hql);
        query.setParameter("name","Uncategorized");
        List results = query.list();
        session.close();
        GifFolder gifFolder;
        if (!results.isEmpty()) {
            gifFolder = (GifFolder)results.get(0);
        } else {
            // initialize the uncategorized folder
            gifFolder = new GifFolder();
            gifFolder.setId(UUID.randomUUID().toString());
            gifFolder.setName("Uncategorized");
            gifFolder.setCreatedAt(new Date());
            ArrayList<GifVault> gifVaultList = new ArrayList();
            gifFolder.setGifVaultEntries(gifVaultList);
            insertNewGifFolder(gifFolder);
        }
        return gifFolder;
    }
}
