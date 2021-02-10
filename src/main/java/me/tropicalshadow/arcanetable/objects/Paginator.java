package me.tropicalshadow.arcanetable.objects;

import me.tropicalshadow.arcanetable.utils.Logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Paginator {
    public List<Object> items = new ArrayList<>();
    public Class<?> itemClass;
    public int currentPage;
    public int itemCount;
    public int pageCount;
    public int pageSize;

    public Paginator(Class<?> itemClass, int pageSize) throws Exception{
        this.itemClass = itemClass;
        if(pageSize <=0){
            throw new Exception("Invalid size of pages");
        }
        this.pageSize = pageSize;
        this.currentPage = 0;
        this.itemCount = 0;
        this.pageCount = 0;

    }

    public Paginator addItem(Object obj) throws Exception {
        if( itemClass.isInstance(obj)){
            items.add(obj);
            updateStats();
            return this;
        }
        throw new Exception("addItem() invalid Object passed");
    }

    public Paginator addItems(Object... objs) {
        return this.addItems(Arrays.asList(objs));
    }
    public Paginator addItems(ArrayList<?> objs) {
        for (Object obj : objs) {
            try{
                addItem(obj);
            }catch (Exception e){
                Logging.danger("Issue while adding items to paginator");
                e.printStackTrace();
            }
        }
        updateStats();
        return this;
    }

    public Paginator addItems(List<?> objs) {
        return this.addItems(new ArrayList<>(objs));
    }

    public Paginator clear(){
        items.clear();
        itemCount = 0;
        pageCount = 0;
        return this;
    }

    public void updateStats(){
        this.itemCount = items.size();
        this.pageCount = this.itemCount/pageSize;
        if(this.currentPage>this.pageCount && this.pageCount>=1){
            this.currentPage=this.pageCount;
        }else if(this.currentPage<0){
            this.currentPage = 0;
        }
    }

    public int getCurrentPageNumber() {
        return currentPage;
    }
    public List<?> getCurrentPage(){
        List<Object> output = new ArrayList<>();
        int pageNum = this.getCurrentPageNumber();
        int startIndex = pageNum==0 ? 0 : (this.pageSize*(pageNum));
        int endIndex = pageNum==0 ? this.pageSize-1 : (this.pageSize*(pageNum+1))-1;
        for (int i = startIndex; i <= endIndex; i++) {
            if(i >= this.items.size()){
                break;
            }
            output.add(this.items.get(i));
        }
        return output;
    }

    public int getPageCount() {
        updateStats();
        return pageCount;
    }

    public Paginator nextPage(){
        updateStats();
        if(currentPage!=pageCount)currentPage++;
        return this;
    }
    public Paginator prevPage(){
        updateStats();
        if(currentPage!=0)currentPage--;
        return this;
    }
    public Paginator setPage(int num){
        updateStats();
        currentPage=num;
        return this;
    }

}
