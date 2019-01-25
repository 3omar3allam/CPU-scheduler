package scheduler;

class ChartSegment {
    String name;
    int start;
    int length;
    String color;

    ChartSegment(String name, int start, int length,String color){
        this.name = name;
        this.start = start;
        this.length = length;
        this.color = color;
    }
    ChartSegment(ChartSegment segment){
        this.name = segment.name;
        this.start = segment.start;
        this.color = segment.color;
    }
}

