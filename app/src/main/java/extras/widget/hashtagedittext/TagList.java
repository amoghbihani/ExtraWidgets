package extras.widget.hashtagedittext;

import java.util.LinkedList;

class TagList extends LinkedList {
    public void insert(Tag tag) {
        for (int i = 0; i < size(); ++i) {
            if (tag.end() < tag.start()) {
                continue;
            } else if (getTag(i).start() + tag.length() >= tag.end()) {
                add(i, tag);
                return;
            }
        }
        add(tag);
    }

    public void updateEntries(int start, int length) {
        for (int i = 0; i < size(); ++i) {
            if (getTag(i).start() < start) {
                continue;
            }
            getTag(i).updateMarkers(length);
        }
    }

    public Tag getContainingTag(int pos) {
        for (int i = 0; i < size(); ++i) {
            if (getTag(i).start() <= pos && getTag(i).end() >= pos) {
                return getTag(i);
            }
        }
        return null;
    }

    public int getPreviousTagEnd(int pos) {
        int end = 0;
        for (int i = size() - 1; i >= 0; --i) {
            if (getTag(i).end() < pos) {
                end = getTag(i).end();
                break;
            }
        }
        return end;
    }

    public Tag getTag(int index) {
        return (Tag) get(index);
    }
}
