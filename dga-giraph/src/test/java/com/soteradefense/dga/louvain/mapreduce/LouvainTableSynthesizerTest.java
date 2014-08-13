package com.soteradefense.dga.louvain.mapreduce;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class LouvainTableSynthesizerTest {

    MapDriver<LongWritable, Text, Text, Text> mapDriver;
    ReduceDriver<Text, Text, Text, NullWritable> reduceDriver;

    @Before
    public void setUp() {
        LouvainTableSynthesizerMapper mapper = new LouvainTableSynthesizerMapper();
        mapDriver = MapDriver.newMapDriver(mapper);
        LouvainTableSynthesizerReducer reducer = new LouvainTableSynthesizerReducer();
        reduceDriver = ReduceDriver.newReduceDriver(reducer);
    }


    @Test
    public void testMapper() throws Exception {
        mapDriver.setMapInputPath(new Path("/user/tempUser/louvainTest/giraph_0/file.txt"));
        mapDriver.withInput(new LongWritable(), new Text("1\t2\t0\t2:1"));
        mapDriver.withOutput(new Text("2"), new Text("1\t2:0"));
        mapDriver.runTest();
    }

    @Test
    public void testMapperGiraphNonZero() throws Exception {
        mapDriver.setMapInputPath(new Path("/user/tempUser/louvainTest/giraph_1/file.txt"));
        mapDriver.withInput(new LongWritable(), new Text("1\t2\t0\t2:1"));
        mapDriver.withOutput(new Text("1"), new Text("2:1"));
        mapDriver.runTest();
    }

    @Test
    public void testMapperGiraphTemp() throws Exception {
        mapDriver.setMapInputPath(new Path("/user/tempUser/louvainTest/table_0/file.txt"));
        mapDriver.withInput(new LongWritable(), new Text("1\t2\t3\t4\t5\t6"));
        mapDriver.withOutput(new Text("6"), new Text("1\t2\t3\t4\t5\t6:0"));
        mapDriver.runTest();
    }

    @Test
    public void testReducer() throws Exception {
        List<Text> list = new LinkedList<Text>();
        list.add(new Text("1\t2:0"));
        list.add(new Text("2\t2:0"));
        list.add(new Text("3\t2:0"));
        list.add(new Text("6:1"));
        list.add(new Text("5\t2:0"));
        reduceDriver.addInput(new Text("2"), list);
        reduceDriver.addOutput(new Text("1\t2\t6"), NullWritable.get());
        reduceDriver.addOutput(new Text("2\t2\t6"), NullWritable.get());
        reduceDriver.addOutput(new Text("3\t2\t6"), NullWritable.get());
        reduceDriver.addOutput(new Text("5\t2\t6"), NullWritable.get());
        List<Pair<Text, NullWritable>> results = reduceDriver.run();
        assertTrue(results.size() == 4);
    }
}
