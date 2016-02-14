/**
 * Copyright 2014 Joan Zapata
 *
 * This file is part of Android-pdfview.
 *
 * Android-pdfview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android-pdfview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android-pdfview.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.joanzapata.pdfview;

import android.graphics.RectF;
import com.joanzapata.pdfview.model.PagePart;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;

import static com.joanzapata.pdfview.util.Constants.Cache.*;

class CacheManager {


    private Vector<PagePart> thumbnails;

    public CacheManager() {
        thumbnails = new Vector<>();
    }

    public void cacheThumbnail(PagePart part) {

        // If cache too big, remove and recycle
        /**if (thumbnails.size() >= THUMBNAILS_CACHE_SIZE) {
            thumbnails.remove(0).getRenderedBitmap().recycle();
        }**/

        // Then add thumbnail
        thumbnails.add(part);

    }

    /** Return true if already contains the described PagePart */
    public boolean containsThumbnail(int userPage, int page, float width, float height, RectF pageRelativeBounds) {
        PagePart fakePart = new PagePart(userPage, page, null, width, height, pageRelativeBounds);
        for (PagePart part : thumbnails) {
            if (part.equals(fakePart)) {
                return true;
            }
        }
        return false;
    }


    public Vector<PagePart> getThumbnails() {
        return thumbnails;
    }

    public void recycle() {
        for (PagePart part : thumbnails) {
            part.getRenderedBitmap().recycle();
        }
        thumbnails.clear();
    }
}
