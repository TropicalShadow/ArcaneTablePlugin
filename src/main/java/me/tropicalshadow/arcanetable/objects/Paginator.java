package me.tropicalshadow.arcanetable.objects;

import me.tropicalshadow.arcanetable.utils.Logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Paginator<T> {
    private final List<T> items = new ArrayList<>();
    private int currentPage;
    private int itemCount;
    private int pageCount;
    private final int pageSize;

    public Paginator(int pageSize) throws IllegalArgumentException {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Invalid page size");
        }
        this.pageSize = pageSize;
        this.currentPage = 0;
        this.itemCount = 0;
        this.pageCount = 0;
    }

    public Paginator<T> addItem(T obj) throws IllegalArgumentException {
        items.add(obj);
        updateStats();
        return this;
    }

    public Paginator<T> addItems(T... objs) {
        return this.addItems(Arrays.asList(objs));
    }

    public Paginator<T> addItems(ArrayList<T> objs) {
        for (T obj : objs) {
            try {
                addItem(obj);
            } catch (IllegalArgumentException e) {
                Logging.danger("Issue while adding items to paginator");
                e.printStackTrace();
            }
        }
        updateStats();
        return this;
    }

    public Paginator<T> addItems(List<T> objs) {
        return this.addItems(new ArrayList<>(objs));
    }

    public Paginator<T> clear() {
        items.clear();
        itemCount = 0;
        pageCount = 0;
        return this;
    }

    private void updateStats() {
        this.itemCount = items.size();
        this.pageCount = (int) Math.ceil((double) this.itemCount / pageSize);
        if (this.currentPage > this.pageCount && this.pageCount >= 1) {
            this.currentPage = this.pageCount;
        } else if (this.currentPage < 0) {
            this.currentPage = 0;
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<T> getCurrentPageContent() {
        List<T> output = new ArrayList<>();
        int pageNum = this.getCurrentPage();
        int startIndex = pageNum == 0 ? 0 : (this.pageSize * pageNum);
        int endIndex = pageNum == 0 ? this.pageSize - 1 : (this.pageSize * (pageNum + 1)) - 1;
        for (int i = startIndex; i <= endIndex && i < this.items.size(); i++) {
            output.add(this.items.get(i));
        }
        return output;
    }

    public int getPageCount() {
        updateStats();
        return pageCount;
    }

    public Paginator<T> nextPage() {
        updateStats();
        if (currentPage != pageCount) {
            currentPage++;
        }
        return this;
    }

    public Paginator<T> prevPage() {
        updateStats();
        if (currentPage != 0) {
            currentPage--;
        }
        return this;
    }

    public Paginator<T> setPage(int num) {
        updateStats();
        currentPage = Math.max(0, Math.min(num, pageCount - 1));
        return this;
    }
}
