import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import java.util.Scanner;

public class simulator {
	static int pipeline_deepth = 128;
	static int clk;
	static int set_clk;
	static boolean gui = false;
	static boolean conf = false;
	static String conffile;
	static String inputdata;
	static String inputdata0;
	static String outputfile;
	static int conflimit;
	static int looklimit0;
	static int looklimit1;

	static boolean stop;

	static int buf_size;
	static int bandwidth;
	static int look_buf_size;

	static PrintWriter writer;
	static int stallcounter;
	static int column_selector;
	// static int preprocesscounter;
	// static int end_area;
	// static int area_pipleline;

	static StringBuffer buf = new StringBuffer("");

	static ArrayList<ArrayList> input_data = new ArrayList<ArrayList>();
	// new set of parameters for seperate
	static ArrayList<ArrayList> real_input = new ArrayList<ArrayList>();
	static ArrayList<ArrayList> search_table0 = new ArrayList<ArrayList>();
	static ArrayList<ArrayList> search_table1 = new ArrayList<ArrayList>();
	static ArrayList<ArrayList> out_table = new ArrayList<ArrayList>();
	// new set of parameters for seperate
	static ArrayList<ArrayList> input_data0 = new ArrayList<ArrayList>();
	static ArrayList<String> list = new ArrayList<String>();
	static ArrayList<ArrayList> conflist = new ArrayList<ArrayList>();
	static ArrayList<ArrayList> tmp = new ArrayList<ArrayList>();
	static comparator[] pipeline = new comparator[pipeline_deepth];
	private static String ROOT_DIR = "Simulator";

	static boolean done = false;
	static boolean alldone = false;
	static boolean finish = false;

	public simulator() {
		// comparator[] pipeline= new comparator[pipeline_deepth];
	}

	public void configure(ArrayList<ArrayList> conf, int i) {
		if (i < conf.size()) {
			tmp.add(conf.get(i));
			for (int q = tmp.size() - 1; q >= 0; q--) {
				tmp.get(q).set(
						tmp.get(q).size() - 1,
						Integer.toString(Integer.parseInt((String) tmp.get(q).get(tmp.get(q).size() - 1)) - 1));
			}
		} else {
			for (int j = 0; j < conf.size(); j++) {
				ArrayList<String> tmp0;
				tmp0 = tmp.get(conf.size() - j - 1);
				if (!list.get(0).equalsIgnoreCase("nested block join")) {
					ArrayList left = new ArrayList();
					left.add(Integer.parseInt(tmp0.get(1).substring(1, tmp0.get(1).length()-1)));
					// ArrayList<ArrayList> jn = new ArrayList<ArrayList>();
					ArrayList jn = new ArrayList();
					if (!list.get(0).equalsIgnoreCase("sorter")) {
						java.util.List<String> item2 = new ArrayList();
						item2 = Arrays.asList(tmp0.get(7)
								.substring(1, tmp0.get(7).length() - 1)
								.split(","));
						ArrayList array_tmp0 = new ArrayList();
						for (int f = 0; f < item2.size(); f++) {
							// array_tmp0.add(item2.get(f));
							jn.add(item2.get(f));
						}
					}
					pipeline[j] = new comparator(Integer.parseInt(tmp0.get(0)),
							left, tmp0.get(2), tmp0.get(3), tmp0.get(4),
							tmp0.get(5), jn, Integer.parseInt(tmp0.get(6)));
					
				} else {
					// System.out.println(list.get(0));
					ArrayList jn = new ArrayList();
					ArrayList tmp10 = new ArrayList();
					tmp10.add(tmp0.get(1));
					pipeline[j] = new comparator(Integer.parseInt(tmp0.get(0)),
							tmp10, tmp0.get(2), tmp0.get(3), tmp0.get(4),
							tmp0.get(5), jn, Integer.parseInt(tmp0.get(6)));
				}
			}
		}

	}

	// bingyi Sep 9th
	public static void seperate_file() {
		if (list.get(0).equalsIgnoreCase("sorter")) {
			System.out.println("let us see what is the list");
			System.out.println(list);
			int sel_col = Integer.parseInt(list.get(1));
			java.util.List<String> item0 = new ArrayList();
			item0 = Arrays.asList(list.get(list.size() - 1)
					.substring(1, list.get(list.size() - 1).length() - 1)
					.split(","));

			for (int c = 0; c < input_data.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				real_input.add(em);
				real_input.get(c).add(c);
				real_input.get(c).add(
						input_data.get(c)
								.get(Integer.parseInt(item0.get(sel_col)))
								.toString());
				if (c < look_buf_size) {
					real_input.get(c).add("true");
				} else {
					real_input.get(c).add("false");
				}
			}
			for (int c = 0; c < input_data.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				em.add(c);
				search_table0.add(em);
				for (int f = 0; f < item0.size(); f++) {
					search_table0.get(c).add(
							input_data.get(c)
									.get(Integer.parseInt(item0.get(f)))
									.toString());
				}
			}
		} else if (list.get(0).equalsIgnoreCase("nested block join")) {
			// need to change
			System.out.println("we are in nested block join seperate buf");

			int sel_col0 = Integer.parseInt(list.get(1));
			int sel_col1 = Integer.parseInt(list.get(2));

			ArrayList sel_arr = new ArrayList();
			ArrayList sel_arr_1 = new ArrayList();
			java.util.List<String> item0 = new ArrayList();
			item0 = Arrays.asList(list.get(list.size() - 1)
					.substring(1, list.get(list.size() - 1).length() - 1)
					.split(","));
			for (int f = 0; f < item0.size(); f++) {
				java.util.List<String> item1 = new ArrayList();
				item1 = Arrays.asList(item0.get(f)
						.substring(0, item0.get(f).length()).split("-"));
				if (item1.get(0).equals("1") || item1.get(0).equals(" 1")) {
					sel_arr.add(item1.get(1));
				} else if (item1.get(0).equals("2")
						|| item1.get(0).equals(" 2")) {
					sel_arr_1.add(item1.get(1));
				}
			}

			for (int c = 0; c < input_data0.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				real_input.add(em);
				real_input.get(c).add(c);
				real_input.get(c).add(
						input_data0.get(c).get(sel_col1).toString());
				if (c < look_buf_size) {
					real_input.get(c).add("true");
				} else {
					real_input.get(c).add("false");
				}
			}
			System.out.println("real input data is as following");
			System.out.println(real_input);

			for (int c = 0; c < input_data.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				em.add(c);
				search_table0.add(em);
				System.out.println("input is here!");
				System.out.println(input_data);
				System.out.println("input0 is here!!");
				System.out.println(input_data0);
				for (int f = 0; f < sel_arr.size(); f++) {
					search_table0.get(c).add(
							input_data
									.get(c)
									.get(Integer.parseInt((String) sel_arr
											.get(f))).toString());
				}
			}
			for (int c = 0; c < input_data0.size(); c++) {

				ArrayList em = new ArrayList();
				em.add(c);
				search_table1.add(em);
				for (int f = 0; f < sel_arr_1.size(); f++) {
					search_table1.get(c).add(
							input_data0
									.get(c)
									.get(Integer.parseInt((String) sel_arr_1
											.get(f))).toString());
				}
				if (c < look_buf_size) {
					search_table1.get(c).add("true");
				} else {
					search_table1.get(c).add("false");
				}
			}
		} else {
			for (int c = 0; c < input_data.size(); c++) {

				ArrayList em = new ArrayList();
				real_input.add(em);
				// real_input.get(c).add(c);
				for (int ct = 0; ct < input_data.get(c).size(); ct++) {
					real_input.get(c).add(input_data.get(c).get(ct));
				}
				real_input.get(c).add("true");
			}
		}
	}

	// bingyi Sep 9th

	// bingyi's new code
	public static void seperate(StringBuffer buf) {
		if (list.get(0).equalsIgnoreCase("sorter")) {
			System.out.println("let us see what is the list");
			System.out.println(list);
			int sel_col = Integer.parseInt(list.get(1));
			java.util.List<String> item0 = new ArrayList();
			item0 = Arrays.asList(list.get(list.size() - 1)
					.substring(1, list.get(list.size() - 1).length() - 1)
					.split(","));
			buf.append("<div>");
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Real Input Data, haha" + "</div>\n");
			for (int c = 0; c < input_data.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				real_input.add(em);
				real_input.get(c).add(c);
				real_input.get(c).add(
						input_data.get(c)
								.get(Integer.parseInt(item0.get(sel_col)))
								.toString());
				if (c < look_buf_size) {
					real_input.get(c).add("true");
				} else {
					real_input.get(c).add("false");
				}
				for (int f = 0; f < real_input.get(c).size(); f++) {
					if (real_input.get(c).get(real_input.get(c).size() - 1)
							.equals("true")) {
						color = "green";
					} else {
						color = "red";
					}
					String cname = (real_input.get(c).get(f)).toString();
					buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname + "</div>\n");
				}
				// String cname = Integer.toString(c);
				// buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
				// + color + "\">" + cname + "</div>\n");
				// cname =
				// input_data.get(c).get(Integer.parseInt(item0.get(sel_col))).toString();
				// buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
				// + color + "\">" + cname + "</div>\n");

				buf.append("    <div style=\"clear:both;\"></div>\n");
			}
			buf.append("<div>");
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "New Data Table" + "</div>\n");
			for (int c = 0; c < input_data.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				em.add(c);
				search_table0.add(em);
				for (int f = 0; f < item0.size(); f++) {
					search_table0.get(c).add(
							input_data.get(c)
									.get(Integer.parseInt(item0.get(f)))
									.toString());
				}
				for (int f = 0; f < search_table0.get(c).size(); f++) {
					String cname = (search_table0.get(c).get(f)).toString();
					buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname + "</div>\n");
				}
				buf.append("    <div style=\"clear:both;\"></div>\n");
			}
			buf.append("   </div>\n");
		} else if (list.get(0).equalsIgnoreCase("nested block join")) {
			// need to change
			System.out.println("we are in nested block join seperate buf");

			int sel_col0 = Integer.parseInt(list.get(1));
			int sel_col1 = Integer.parseInt(list.get(2));

			ArrayList sel_arr = new ArrayList();
			ArrayList sel_arr_1 = new ArrayList();
			java.util.List<String> item0 = new ArrayList();
			item0 = Arrays.asList(list.get(list.size() - 1)
					.substring(1, list.get(list.size() - 1).length() - 1)
					.split(","));
			for (int f = 0; f < item0.size(); f++) {
				java.util.List<String> item1 = new ArrayList();
				item1 = Arrays.asList(item0.get(f)
						.substring(0, item0.get(f).length()).split("-"));
				if (item1.get(0).equals("1") || item1.get(0).equals(" 1")) {
					sel_arr.add(item1.get(1));
				} else if (item1.get(0).equals("2")
						|| item1.get(0).equals(" 2")) {
					sel_arr_1.add(item1.get(1));
				}
			}
			// System.out.println("haha, let us see the sel_arr");
			// System.out.println(sel_arr);
			// System.out.println("let us see the sel_arr_1");
			// System.out.println(sel_arr_1);
			buf.append("<div>");
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Real Input Data" + "</div>\n");
			// System.out.println("let us see input_data0.size");
			// System.out.println(input_data0);
			for (int c = 0; c < input_data0.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				real_input.add(em);
				real_input.get(c).add(c);
				real_input.get(c).add(
						input_data0.get(c).get(sel_col1).toString());
				if (c < look_buf_size) {
					real_input.get(c).add("true");
				} else {
					real_input.get(c).add("false");
				}
				for (int f = 0; f < real_input.get(c).size(); f++) {
					if (real_input.get(c).get(real_input.get(c).size() - 1)
							.equals("true")) {
						color = "green";
					} else {
						color = "red";
					}
					String cname = (real_input.get(c).get(f)).toString();
					buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname + "</div>\n");
				}
				buf.append("    <div style=\"clear:both;\"></div>\n");
			}
			System.out.println("real input data is as following");
			System.out.println(real_input);
			buf.append("<div>");
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Search Table 0" + "</div>\n");
			for (int c = 0; c < input_data.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				em.add(c);
				search_table0.add(em);
				System.out.println("input is here!");
				System.out.println(input_data);
				System.out.println("input0 is here!!");
				System.out.println(input_data0);
				for (int f = 0; f < sel_arr.size(); f++) {
					search_table0.get(c).add(
							input_data
									.get(c)
									.get(Integer.parseInt((String) sel_arr
											.get(f))).toString());
				}
				for (int f = 0; f < search_table0.get(c).size(); f++) {
					String cname = (search_table0.get(c).get(f)).toString();
					buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname + "</div>\n");
				}
				buf.append("    <div style=\"clear:both;\"></div>\n");
			}
			buf.append("   </div>\n");
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Search Table 1" + "</div>\n");
			for (int c = 0; c < input_data0.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				em.add(c);
				search_table1.add(em);
				for (int f = 0; f < sel_arr_1.size(); f++) {
					search_table1.get(c).add(
							input_data0
									.get(c)
									.get(Integer.parseInt((String) sel_arr_1
											.get(f))).toString());
				}
				if (c < look_buf_size) {
					search_table1.get(c).add("true");
				} else {
					search_table1.get(c).add("false");
				}
				for (int f = 0; f < search_table1.get(c).size(); f++) {
					if (real_input.get(c).get(real_input.get(c).size() - 1)
							.equals("true")) {
						color = "green";
					} else {
						color = "red";
					}
					String cname = (search_table1.get(c).get(f)).toString();
					buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname + "</div>\n");
				}
				buf.append("    <div style=\"clear:both;\"></div>\n");
			}
			buf.append("   </div>\n");

		} else {
			for (int c = 0; c < input_data.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				real_input.add(em);
				// real_input.get(c).add(c);
				for (int ct = 0; ct < input_data.get(c).size(); ct++) {
					real_input.get(c).add(input_data.get(c).get(ct));
				}
				real_input.get(c).add("true");
				for (int f = 0; f < real_input.get(c).size(); f++) {
					if (real_input.get(c).get(real_input.get(c).size() - 1)
							.equals("true")) {
						color = "green";
					} else {
						color = "red";
					}
					String cname = (real_input.get(c).get(f)).toString();
					buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname + "</div>\n");
				}
				buf.append("    <div style=\"clear:both;\"></div>\n");
			}
			buf.append("<div>");
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "New Data Table" + "</div>\n");
		}
	}

	// bingyi's new code

	// bingyi's newest code
	public static void output(StringBuffer buf) {
		if (list.get(0).equals("nested block join")) {
			for (int c = 0; c < search_table0.size(); c++) {
				buf.append("<div>");
				String color = "white";
				ArrayList em = new ArrayList();
				em.add(c);
				for (int f = 0; f < search_table0.get(c).size(); f++) {
					// search_table0.get(c).add(input_data.get(c).get(Integer.parseInt(item0.get(f))).toString());
					String cname = (search_table0.get(c).get(f)).toString();
					buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname + "</div>\n");
				}
				buf.append("    <div style=\"clear:both;\"></div>\n");
			}
			buf.append("   </div>\n");
			buf.append("<div>");

			buf.append("   </div>\n");
			buf.append("<div>");
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Search Table 0" + "</div>\n");
			buf.append("    <div style=\"clear:both;\"></div>\n");
			buf.append("   </div>\n");
			if (list.get(0).equalsIgnoreCase("nested block join")) {

				for (int c = 0; c < search_table1.size(); c++) {
					String color = "white";
					ArrayList em = new ArrayList();
					em.add(c);
					for (int f = 0; f < search_table1.get(c).size(); f++) {
						if (search_table1.get(c)
								.get(search_table1.get(c).size() - 1)
								.equals("true")) {
							color = "green";
						} else {
							color = "red";
						}
						// search_table0.get(c).add(input_data.get(c).get(Integer.parseInt(item0.get(f))).toString());
						String cname = (search_table1.get(c).get(f)).toString();
						buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
								+ color + "\">" + cname + "</div>\n");
					}
					buf.append("    <div style=\"clear:both;\"></div>\n");

				}
				buf.append("   </div>\n");
				buf.append("<div>");
				buf.append("<div>");
				buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
						+ "Search Table 1" + "</div>\n");
			}
			buf.append("    <div style=\"clear:both;\"></div>\n");
			buf.append("   </div>\n");
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Output Table" + "</div>\n");
			buf.append("    <div style=\"clear:both;\"></div>\n");
			for (int c = 0; c < out_table.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				em.add(c);
				for (int f = 0; f < out_table.get(c).size(); f++) {
					// search_table0.get(c).add(input_data.get(c).get(Integer.parseInt(item0.get(f))).toString());
					String cname = (out_table.get(c).get(f)).toString();
					buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname + "</div>\n");
				}
				buf.append("    <div style=\"clear:both;\"></div>\n");
			}
			buf.append("   </div>\n");
		} else {

			buf.append("    <div style=\"clear:both;\"></div>\n");
			buf.append("   </div>\n");
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Output Table" + "</div>\n");
			buf.append("    <div style=\"clear:both;\"></div>\n");
			for (int c = 0; c < out_table.size(); c++) {
				String color = "white";
				ArrayList em = new ArrayList();
				em.add(c);
				for (int f = 0; f < out_table.get(c).size(); f++) {
					// search_table0.get(c).add(input_data.get(c).get(Integer.parseInt(item0.get(f))).toString());
					String cname = (out_table.get(c).get(f)).toString();
					buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname + "</div>\n");
				}
				buf.append("    <div style=\"clear:both;\"></div>\n");
			}
			buf.append("   </div>\n");

		}

	}

	// bingyi's newest code

	public static void ConfiguretoHtml(StringBuffer buf) {

		buf.append("<div>");
		buf.append("<div style=\"width: 1000px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
				+ "Configuration" + "</div>\n");
		for (int q = tmp.size() - 1; q >= 0; q--) {
			if (clk < conflist.size() + 1) {
				buf.append("<div style=\"width: 60px; height: 20px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid white; background-color: "
						+ "white" + "\">" + "==>" + "</div>\n");
			} else {
				buf.append("    <div style=\"width: 60px; height: 20px; float:left;\"></div>\n");
			}
			for (int c = 0; c < conflist.get(q).size(); c++) {
				String color = COMPONENTS_COLORS[c];
				String cname = (String) conflist.get(q).get(c);
				buf.append("<div style=\"width: 50px; height: 20px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname + "</div>\n");
			}
		}
		buf.append("    <div style=\"clear:both;\"></div>\n");

	}

	public void write_out(String name) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(name, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println(list);
		writer.println(input_data);
		if (list.get(0).equalsIgnoreCase("nested block join")) {
			writer.println(input_data0);
		}
		StringBuffer tmp = new StringBuffer();
		if (list.get(0).equalsIgnoreCase("sorter")) {
			for (int j = 0; j < conflist.size(); j++) {
				pipeline[j].get_sort_state(tmp);
			}
		}
		if (list.get(0).equalsIgnoreCase("parallel processing")) {
			for (int j = 0; j < conflist.size(); j++) {
				pipeline[j].get_parallel_state(tmp);
			}
		}
		if (list.get(0).equalsIgnoreCase("nested block join")) {
			for (int j = 0; j < conflist.size(); j++) {
				pipeline[j].get_nested_state(tmp);
			}
		}
		if (list.get(0).equalsIgnoreCase("join and")) {
			for (int j = 0; j < conflist.size(); j++) {
				pipeline[j].get_joinand_state(tmp);
			}
		}
		if (list.get(0).equalsIgnoreCase("join or")) {
			for (int j = 0; j < conflist.size(); j++) {
				pipeline[j].get_joinor_state(tmp);
			}
		}
		writer.println(tmp);
		writer.close();
	}
	
	public class CustomComparator implements Comparator<ArrayList> {
	    @Override
	    public int compare(ArrayList o1, ArrayList o2) {
	        return Integer.parseInt((String) o1.get(column_selector))<Integer.parseInt((String) (o2.get(column_selector)))?1:0;
	    }
	}
	
	
	public void supposedoutput() {
		ArrayList<ArrayList> copyinput = new ArrayList<ArrayList>();
		PrintWriter correctout = null;
		
		try {
			correctout = new PrintWriter("corretoutput", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.get(0).equalsIgnoreCase("sorter")) {
			java.util.List<String> item0 = new ArrayList();
			item0 = Arrays.asList(list.get(list.size() - 1)
					.substring(1, list.get(list.size() - 1).length() - 1)
					.split(","));
			String order = list.get(2);
			final int column_selector = Integer.parseInt(list.get(1));
			
			
			class CustomComparator1 implements Comparator<ArrayList> {
			    @Override
			    public int compare(ArrayList o1, ArrayList o2) {
			        return (Integer)(o1.get(column_selector))>(Integer)(o2.get(column_selector))?1:0;
			    }
			}
			copyinput = input_data;
			if (list.get(2).equalsIgnoreCase("increasing")) {
			Collections.sort(copyinput, new CustomComparator1());
			}
			else {
				class CustomComparator0 implements Comparator<ArrayList> {
				    @Override
				    public int compare(ArrayList o1, ArrayList o2) {
				        return (Integer)(o1.get(column_selector))<(Integer)(o2.get(column_selector))?1:0;
				    }
				}
			
				Collections.sort(copyinput, new CustomComparator0());
			}
			for (int i=0; i<copyinput.size(); i++) {
				for (int j=0; j<item0.size(); j++) {
				correctout.print(copyinput.get(i).get(Integer.parseInt(item0.get(j))));
				correctout.print(" ");
				}
				correctout.print("\n");
			}	
			correctout.close();
		}
		if (list.get(0).equalsIgnoreCase("parallel processing")) {
			for (int i = 1; i < list.size()-1; i++) {
					
					ArrayList<String> tmpparal = new ArrayList<String>();
					
					String text = list.get(i);
					Scanner fi = new Scanner(text);
					fi.useDelimiter(" ");
					while (fi.hasNext()) {
						tmpparal.add(fi.next());
					}
					/*if (tmp.get(1).equals("<")) {
						//conf_e.add("smaller");
					} else if (tmp.get(1).equals("=")) {
						//conf_e.add("equal_w");
					} else if (tmp.get(1).equals(">")) {
						//conf_e.add("larger");
					}
				*/
					// System.out.println(list.get(conf_size-1));
					java.util.List<String> item3 = new ArrayList();
					item3 = Arrays.asList(list.get(list.size() - 1)
							.substring(1, list.get(list.size() - 1).length() - 1)
							.split("-"));
					for (int j=0; j<input_data.size(); j++){
						if (tmpparal.get(1).equals("<")) {
							if ((Integer)(input_data.get(j).get(Integer.parseInt(tmpparal.get(0)))) < Integer.parseInt(tmpparal.get(2))) {
								String itemunit;
								itemunit = item3.get(i - 1);
								java.util.List<String> itemtmp = new ArrayList();
								itemtmp = Arrays.asList(itemunit.substring(1, itemunit.length() - 1).split(","));
								correctout.print(i-1);
								correctout.print(" ");
							for (int f=0; f<itemtmp.size(); f++) {
								
									correctout.print(input_data.get(j).get(Integer.parseInt(itemtmp.get(f))));
									correctout.print(" ");
								}
							correctout.print("\n");
							}
						} else if (tmpparal.get(1).equals("=")) {
							
								if ((Integer)(input_data.get(j).get(Integer.parseInt(tmpparal.get(0)))) == Integer.parseInt(tmpparal.get(2))) {
									String itemunit;
									itemunit = item3.get(i - 1);
									java.util.List<String> itemtmp = new ArrayList();
									itemtmp = Arrays.asList(itemunit.substring(1, itemunit.length() - 1).split(","));
									correctout.print(i-1);
									correctout.print(" ");
									for (int f=0; f<itemtmp.size(); f++) {
										correctout.print(input_data.get(j).get(Integer.parseInt(itemtmp.get(f))));
										correctout.print(" ");
									}
								correctout.print("\n");
								}
						} else if (tmpparal.get(1).equals(">")) {
							
								if ((Integer)(input_data.get(j).get(Integer.parseInt(tmpparal.get(0)))) > Integer.parseInt(tmpparal.get(2))) {
									String itemunit;
									itemunit = item3.get(i - 1);
									java.util.List<String> itemtmp = new ArrayList();
									itemtmp = Arrays.asList(itemunit.substring(1, itemunit.length() - 1).split(","));
									correctout.print(i-1);
									correctout.print(" ");
									for (int f=0; f<itemtmp.size(); f++) {
										correctout.print(input_data.get(j).get(Integer.parseInt(itemtmp.get(f))));
										correctout.print(" ");
									}
								correctout.print("\n");
								}
						}
						
					}
					//conf_e.add(item3.get(i - 1));
					//conflist.add(conf_e);
				}
			correctout.close();
		}
		}

		/*if (list.get(0).equalsIgnoreCase("nested block join")) {
			int column_selector = Integer.parseInt(list.get(1));
			if (input_data.size() > pipeline_deepth) {
				System.out
						.println("sorry, we cannot process the nested block join when both table size exceed the pipeline deepth");
			} else {
				data_size = input_data0.size();
				// System.out.println("The data to set up configuration is ");
				// System.out.println(input_data);
				for (int i = 0; i < input_data.size(); i++) {
					// real_input = input_data0;
					ArrayList tmp = new ArrayList<String>();
					ArrayList<String> conf_e = new ArrayList<String>();
					ArrayList text = input_data.get(i);
					conf_e.add(Integer.toString((Integer) text
							.get(column_selector)));
					// tmp=search_table0.get(0);
					conf_e.add(Integer.toString(i));
					// conf_e.add(text.toString());
					conf_e.add("one_fix");
					if (list.get(3).equalsIgnoreCase("<")) {
						conf_e.add("smaller");
					} else if (list.get(3).equalsIgnoreCase(">")) {
						conf_e.add("larger");
					} else if (list.get(3).equalsIgnoreCase("=")) {
						conf_e.add("equal_w");
					}
					if (i > 0) {
						conf_e.add("keepon_enter");
						conf_e.add("keepon");
					} else {
						conf_e.add("keepon_enter_last");
						conf_e.add("keepon_last");
					}
					conf_e.add(Integer.toString(column_selector));
					// conf_e.add(list.get(list.size() - 1));
					conflist.add(conf_e);
					// System.out.println(conf_e);
				}
			}
		}
		if (list.get(0).equalsIgnoreCase("join and")) {
			int conf_size;
			data_size = input_data.size();
			conf_size = list.size();
			if (conf_size > sim.pipeline_deepth) {
				System.out.format(
						"sorry, we can only process less than %d join and\n",
						sim.pipeline_deepth);
			} else {
				for (int i = 1; i < conf_size - 1; i++) {
					ArrayList<String> tmp = new ArrayList<String>();
					ArrayList<String> conf_e = new ArrayList<String>();
					String text = list.get(i);
					Scanner fi = new Scanner(text);
					fi.useDelimiter(" ");
					while (fi.hasNext()) {
						tmp.add(fi.next());
					}
					conf_e.add(tmp.get(2));
					conf_e.add(Integer.toString(0));
					conf_e.add("one_fix");
					if (tmp.get(1).equals("<")) {
						conf_e.add("smaller");
					} else if (tmp.get(1).equals("=")) {
						conf_e.add("equal_w");
					} else if (tmp.get(1).equals(">")) {
						conf_e.add("larger");
					}
					if (i != 1)
						conf_e.add("keepon");
					else
						conf_e.add("enter_buffer");
					conf_e.add("discard");
					conf_e.add(tmp.get(0));
					conf_e.add(list.get(list.size() - 1));
					conflist.add(conf_e);
				}

			}
		}
		if (list.get(0).equalsIgnoreCase("join or")) {
			int conf_size;
			data_size = input_data.size();
			conf_size = list.size();
			if (conf_size > sim.pipeline_deepth) {
				System.out.format(
						"sorry, we can only process less than %d join and\n",
						sim.pipeline_deepth);
			} else {
				for (int i = 1; i < conf_size - 1; i++) {
					ArrayList<String> tmp = new ArrayList<String>();
					ArrayList<String> conf_e = new ArrayList<String>();
					String text = list.get(i);
					Scanner fi = new Scanner(text);
					fi.useDelimiter(" ");
					while (fi.hasNext()) {
						tmp.add(fi.next());
					}
					conf_e.add(tmp.get(2));
					conf_e.add(Integer.toString(0));
					conf_e.add("one_fix");
					if (tmp.get(1).equals("<")) {
						conf_e.add("smaller");
					} else if (tmp.get(1).equals("=")) {
						conf_e.add("equal_w");
					} else if (tmp.get(1).equals(">")) {
						conf_e.add("larger");
					}
					conf_e.add("enter_buffer");
					conf_e.add("keepon");
					conf_e.add(tmp.get(0));
					conf_e.add(list.get(list.size() - 1));
					conflist.add(conf_e);
				}

			}
		}*/
	//}

	public static void composeconfigure(simulator sim, int data_size) {
		if (list.get(0).equalsIgnoreCase("sorter")) {
			String order = list.get(2);
			int column_selector = Integer.parseInt(list.get(1));
			data_size = input_data.size();
			if (data_size > sim.pipeline_deepth) {
				System.out.format(
						"sorry, we can only sort less than %d numbers\n",
						sim.pipeline_deepth);
			} else {

				for (int i = 0; i < data_size; i++) {
					ArrayList<String> tmp = new ArrayList<String>();
					if (order.equalsIgnoreCase("decreasing")) {
						tmp.add(Integer.toString(0));
						tmp.add(Integer.toString(0));
						tmp.add("non_fix");
						tmp.add("smaller");
						tmp.add("keepon");
						tmp.add("keepon");
						// tmp.add(Integer.toString(column_selector));
						tmp.add(Integer.toString(1));
						conflist.add(tmp);
					} else if (order.equalsIgnoreCase("increasing")) {
						tmp.add(Integer.toString(0));
						tmp.add(Integer.toString(0));
						tmp.add("non_fix");
						tmp.add("larger");
						tmp.add("keepon");
						tmp.add("keepon");
						// tmp.add(Integer.toString(column_selector));
						tmp.add(Integer.toString(1));
						conflist.add(tmp);
					}
				}
				look_buf_size = conflist.size();
			}
		}
		if (list.get(0).equalsIgnoreCase("parallel processing")) {
			int conf_size;
			data_size = input_data.size();
			conf_size = list.size();
			if (conf_size > sim.pipeline_deepth) {
				System.out
						.format("sorry, we can only process less than %d parallel queries\n",
								sim.pipeline_deepth);
			} else {
				for (int i = 1; i < conf_size - 1; i++) {
					ArrayList<String> tmp = new ArrayList<String>();
					ArrayList<String> conf_e = new ArrayList<String>();
					String text = list.get(i);
					Scanner fi = new Scanner(text);
					fi.useDelimiter(" ");
					while (fi.hasNext()) {
						tmp.add(fi.next());
					}
					conf_e.add(tmp.get(2));
					ArrayList tmpf = new ArrayList();
					tmpf.add(i);
					// conf_e.add(Integer.toString(i));
					conf_e.add(tmpf.toString());
					conf_e.add("one_fix");
					if (tmp.get(1).equals("<")) {
						conf_e.add("smaller");
					} else if (tmp.get(1).equals("=")) {
						conf_e.add("equal_w");
					} else if (tmp.get(1).equals(">")) {
						conf_e.add("larger");
					}
					// if (i>1) {
					conf_e.add("keepon_enter");
					conf_e.add("keepon");
					// }
					// else {
					// conf_e.add("keepon_enter_last");
					// conf_e.add("keepon_last");
					// }
					// conf_e.add("keepon_enter");
					// conf_e.add("keepon");
					conf_e.add(tmp.get(0));
					// System.out.println(list.get(conf_size-1));
					java.util.List<String> item3 = new ArrayList();
					item3 = Arrays.asList(list.get(conf_size - 1)
							.substring(1, list.get(conf_size - 1).length() - 1)
							.split("-"));

					// System.out.println(item3.get(i - 1));
					conf_e.add(item3.get(i - 1));
					conflist.add(conf_e);
				}

			}
		}
		if (list.get(0).equalsIgnoreCase("nested block join")) {
			int column_selector = Integer.parseInt(list.get(1));
			if (input_data.size() > pipeline_deepth) {
				System.out
						.println("sorry, we cannot process the nested block join when both table size exceed the pipeline deepth");
			} else {
				data_size = input_data0.size();
				// System.out.println("The data to set up configuration is ");
				// System.out.println(input_data);
				for (int i = 0; i < input_data.size(); i++) {
					// real_input = input_data0;
					ArrayList tmp = new ArrayList<String>();
					ArrayList<String> conf_e = new ArrayList<String>();
					ArrayList text = input_data.get(i);
					conf_e.add(Integer.toString((Integer) text
							.get(column_selector)));
					// tmp=search_table0.get(0);
					conf_e.add(Integer.toString(i));
					// conf_e.add(text.toString());
					conf_e.add("one_fix");
					if (list.get(3).equalsIgnoreCase("<")) {
						conf_e.add("smaller");
					} else if (list.get(3).equalsIgnoreCase(">")) {
						conf_e.add("larger");
					} else if (list.get(3).equalsIgnoreCase("=")) {
						conf_e.add("equal_w");
					}
					if (i > 0) {
						conf_e.add("keepon_enter");
						conf_e.add("keepon");
					} else {
						conf_e.add("keepon_enter_last");
						conf_e.add("keepon_last");
					}
					conf_e.add(Integer.toString(column_selector));
					// conf_e.add(list.get(list.size() - 1));
					conflist.add(conf_e);
					// System.out.println(conf_e);
				}
			}
		}
		if (list.get(0).equalsIgnoreCase("join and")) {
			int conf_size;
			data_size = input_data.size();
			conf_size = list.size();
			if (conf_size > sim.pipeline_deepth) {
				System.out.format(
						"sorry, we can only process less than %d join and\n",
						sim.pipeline_deepth);
			} else {
				for (int i = 1; i < conf_size - 1; i++) {
					ArrayList<String> tmp = new ArrayList<String>();
					ArrayList<String> conf_e = new ArrayList<String>();
					String text = list.get(i);
					Scanner fi = new Scanner(text);
					fi.useDelimiter(" ");
					while (fi.hasNext()) {
						tmp.add(fi.next());
					}
					conf_e.add(tmp.get(2));
					conf_e.add(Integer.toString(0));
					conf_e.add("one_fix");
					if (tmp.get(1).equals("<")) {
						conf_e.add("smaller");
					} else if (tmp.get(1).equals("=")) {
						conf_e.add("equal_w");
					} else if (tmp.get(1).equals(">")) {
						conf_e.add("larger");
					}
					if (i != 1)
						conf_e.add("keepon");
					else
						conf_e.add("enter_buffer");
					conf_e.add("discard");
					conf_e.add(tmp.get(0));
					conf_e.add(list.get(list.size() - 1));
					conflist.add(conf_e);
				}

			}
		}
		if (list.get(0).equalsIgnoreCase("join or")) {
			int conf_size;
			data_size = input_data.size();
			conf_size = list.size();
			if (conf_size > sim.pipeline_deepth) {
				System.out.format(
						"sorry, we can only process less than %d join and\n",
						sim.pipeline_deepth);
			} else {
				for (int i = 1; i < conf_size - 1; i++) {
					ArrayList<String> tmp = new ArrayList<String>();
					ArrayList<String> conf_e = new ArrayList<String>();
					String text = list.get(i);
					Scanner fi = new Scanner(text);
					fi.useDelimiter(" ");
					while (fi.hasNext()) {
						tmp.add(fi.next());
					}
					conf_e.add(tmp.get(2));
					conf_e.add(Integer.toString(0));
					conf_e.add("one_fix");
					if (tmp.get(1).equals("<")) {
						conf_e.add("smaller");
					} else if (tmp.get(1).equals("=")) {
						conf_e.add("equal_w");
					} else if (tmp.get(1).equals(">")) {
						conf_e.add("larger");
					}
					conf_e.add("enter_buffer");
					conf_e.add("keepon");
					conf_e.add(tmp.get(0));
					conf_e.add(list.get(list.size() - 1));
					conflist.add(conf_e);
				}

			}
		}
	}

	public static void main(String[] args) throws Exception {
		PrintWriter writer0 = new PrintWriter("data0", "UTF-8");
		PrintWriter writer1 = new PrintWriter("data", "UTF-8");
		Random rand = new Random();
		for (int i=0; i<128; i++) {
			for (int j=0; j<15; j++) {
				writer0.printf("%d ", rand.nextInt(1000));
			}
			writer0.printf("\n");
		}
		for (int i=0; i<128; i++) {
			for (int j=0; j<14; j++) {
				writer1.printf("%d ", rand.nextInt(1000));
			}
			writer1.printf("\n");
		}
		writer0.close();
		writer1.close();
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Do you want to enable the graphic interface? (true/false)");
		gui = keyboard.nextBoolean();
		System.out.println("Could you please specify the configuration file's name?");
		conffile = keyboard.next();
		System.out.println("Could you please specify the data file's name?");
		inputdata = keyboard.next();
		System.out.println("Could you please specify the second data file's name?");
		inputdata0 = keyboard.next();
		System.out.println("Could you please specify the buffer size for the buffer in each unit?");
		buf_size = keyboard.nextInt();
		System.out.println("Could you please specify the bandwidth between each unit?");
		bandwidth = keyboard.nextInt();
		System.out.println("Could you please specify the look aside buffer size on the end of the pipeline?");
		look_buf_size = keyboard.nextInt();
		
		// ArrayList<ArrayList> real_input = null;
		/*if (args.length == 7) {
			gui = Boolean.parseBoolean(args[0]);
			conffile = args[1];
			inputdata = args[2];
			outputfile = args[3];
			buf_size = Integer.parseInt(args[4]);
			bandwidth = Integer.parseInt(args[5]);
			look_buf_size = Integer.parseInt(args[6]);
		}
		if (args.length == 8) {
			gui = Boolean.parseBoolean(args[0]);
			conffile = args[1];
			inputdata = args[2];
			inputdata0 = args[3];
			outputfile = args[4];
			buf_size = Integer.parseInt(args[5]);
			bandwidth = Integer.parseInt(args[6]);
			look_buf_size = Integer.parseInt(args[7]);
		} else {
			System.out
					.println("The input configuration should be gui (true/false), configure file name, input data file(s), output file name");
		}
		*/
		File file = new File(conffile);
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(conffile));
			String text = null;
			while ((text = reader.readLine()) != null) {
				list.add(text);
			}
			reader = new BufferedReader(new FileReader(inputdata));
			while ((text = reader.readLine()) != null) {
				ArrayList<Integer> tmp = new ArrayList<Integer>();
				Scanner fi = new Scanner(text);
				fi.useDelimiter(" ");
				while (true) {
					if (fi.hasNext()) {
						tmp.add(fi.nextInt());
					} else
						break;
				}
				input_data.add(tmp);

			}
			if (list.get(0).equalsIgnoreCase("nested block join")) {
				reader = new BufferedReader(new FileReader(inputdata0));
				while ((text = reader.readLine()) != null) {
					ArrayList<Integer> tmp = new ArrayList<Integer>();
					Scanner fi = new Scanner(text);
					fi.useDelimiter(" ");
					while (true) {
						if (fi.hasNext()) {
							tmp.add(fi.nextInt());
						} else
							break;
					}
					input_data0.add(tmp);

				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
		System.out.println(list);
		simulator sim = new simulator();
		
		int data_size = 0;
		composeconfigure(sim, data_size);
		// if (list.get(0).equalsIgnoreCase("nested block join")
		// || list.get(0).equalsIgnoreCase("nested block"))
		// input_data = real_input;

		BufferedReader buffer = null;

		HTTPServer server = null;
		int refresh = 0;
		char req = 'X';
		// new change
		for (int i = 0; i < conflist.size(); i++) {
			conflist.get(i).add(Integer.toString(conflist.size() - i));
		}
		// new change
		if (gui) {
			server = new HTTPServer();
			int port = server.port();
			System.err.println("Port: " + port);
			while ((req = server.nextRequest(0)) == 'I')
				;
			if (req != 'B')
				throw new Exception("Invalid first request");
		}
		FileOutputStream out = null;
		if (gui) {
			for (File f : directoryFiles("./src/webpages", ".html"))
				f.delete();
			out = new FileOutputStream("./src/webpages/index.html");
			out.write(state().getBytes());
			out.close();
		}
		boolean f = true;

		if (gui) {
			f = true;
			while (server != null) {
				// while (clk<=set_clk) {
				while (clk <= set_clk) {
					if (clk > conflist.size() + 1 && !finish
							&& !list.get(0).equalsIgnoreCase("sorter")) {
						// System.out.println("haha, we are in nested block join, not finish");
						if (done) {
							finish = true;
						}
						if (!sim.pipeline[0].buffer_empty()) {
							if (list.get(0).equalsIgnoreCase(
									"nested block join")) {
								sim.pipeline[0]
										.lookaside_both(real_input,
												search_table0, search_table1,
												out_table);
								finish = false;
							} else {
								out_table.add(sim.pipeline[0]
										.get_buffer_one(bandwidth));
							}

						}
						if (real_input.size() != 0) {
							finish = false;
						}
						int end = -1;
						for (int j = 1; j < conflist.size(); j++) {
							if (!sim.pipeline[j].buffer_empty()) {
								end = j;
							}
						}
						// System.out.println("haha end is here");
						// System.out.println(end);
						if (end >= 0) {
							finish = false;
							// for (int j=0; j<end; j++) {
							for (int j = 0; j < conflist.size() - 1; j++) {
								// if (sim.pipeline[j].buffer_empty()) {
								int rm_counter = 0;
								rm_counter = sim.pipeline[j].push_reverse(
										sim.pipeline[j + 1].get_buffer(),
										bandwidth, buf_size, rm_counter);
								sim.pipeline[j + 1].set_buf_em(rm_counter);

								// }
							}
							// System.out.printf("we are checking the %d pipeline",
							// end);
							sim.pipeline[conflist.size() - 1]
									.check_stall(buf_size);
						}
					}
					// while (clk<=set_clk) {
					if (clk < conflist.size() + 1) {
						// if (clk < 2 &&
						// list.get(0).equalsIgnoreCase("sorter")) {
						// sim.seperate();
						// }
						sim.configure(conflist, clk);
					}
					// else if (clk < input_data.size() + conflist.size()*2+2) {
					else if (!done) {
						boolean all = true;

						if (list.get(0).equalsIgnoreCase("sorter")) {
							if (!sim.pipeline[conflist.size() - 1].get_lf()
									|| !sim.pipeline[conflist.size() - 1]
											.get_rf()) {
								done = true;
							}
						} else {
							if (clk > conflist.size() + real_input.size()) {
								// System.out.println("trying to setup done");
								for (int g = 0; g < conflist.size(); g++) {
									if (!sim.pipeline[g].get_rf()
											|| sim.pipeline[g].get_tmp0f()
											|| real_input.size() != 0) {
										all = false;
									}
								}
								if (all) {
									done = true;
								}
							}
						}

						// bingyi's new code
						stop = false;
						for (int fl = 0; fl < conflist.size(); fl++) {
							if (sim.pipeline[fl].get_stall()) {
								stop = true;
							}
						}
						if (!stop) {
							// System.out.println("gosh, the input size is ");
							// System.out.println(real_input);
							if (real_input.size() != 0) {
								// System.out.println("haha, we are in bingyi's new code");
								// sim.pipeline[0].clock_move(input_data.get(0));
								// input_data.remove(0);

								if (real_input.get(0)
										.get(real_input.get(0).size() - 1)
										.equals("true")) {
									// System.out.println("in the get input stage");
									// ArrayList ex_input = new ArrayList();
									real_input.get(0).remove(
											real_input.get(0).size() - 1);
									if (list.get(0).equalsIgnoreCase("join or")
											|| list.get(0).equalsIgnoreCase(
													"join and")
											|| list.get(0).equalsIgnoreCase(
													"parallel processing")) {
										sim.pipeline[0].clock_move_parallel(
												real_input.get(0), buf_size);
									} else {
										sim.pipeline[0].clock_move(
												real_input.get(0), buf_size);
									}
									// System.out.println(real_input.get(0));
									real_input.remove(0);
								} else {
									sim.pipeline[0].last_clk_move(buf_size);
								}

							} else {
								sim.pipeline[0].last_clk_move(buf_size);
							}

							for (int j = 1; j < conflist.size(); j++) {
								if (pipeline[j - 1].out_f()) {
									// System.out.printf("the %d has a valid output\n",
									// j - 1);
									if (list.get(0).equalsIgnoreCase("join or")
											|| list.get(0).equalsIgnoreCase(
													"parallel processing")
											|| list.get(0).equalsIgnoreCase(
													"join and")) {
										pipeline[j].clock_move_parallel(
												pipeline[j - 1].get_out(),
												buf_size);
									} else {
										pipeline[j].clock_move(
												pipeline[j - 1].get_out(),
												buf_size);
									}
								} else {
									pipeline[j].last_clk_move(buf_size);
								}
							}
						}
					} else if (!alldone
							&& list.get(0).equalsIgnoreCase("sorter")) {
						for (int j = 0; j < conflist.size(); j++) {
							sim.pipeline[j].push_to_buffer(j);
						}
						alldone = true;
					} else if (alldone && !finish
							&& list.get(0).equalsIgnoreCase("sorter")) {
						// System.out.println("test here or not");
						finish = true;
						if (!sim.pipeline[0].buffer_empty()) {
							sim.pipeline[0].lookaside(search_table0, out_table);
							finish = false;
						}
						int end = -1;
						for (int j = 1; j < conflist.size(); j++) {
							if (!sim.pipeline[j].buffer_empty()) {
								end = j;
							}
						}
						// System.out.println("haha end is here");
						// System.out.println(end);
						if (end >= 0) {
							finish = false;
							for (int j = 0; j < end; j++) {
								if (sim.pipeline[j].buffer_empty()) {
									int rm_counter = 0;
									rm_counter = sim.pipeline[j].push_reverse(
											sim.pipeline[j + 1].get_buffer(),
											bandwidth, buf_size, rm_counter);
									sim.pipeline[j + 1].set_buf_em(rm_counter);

								}
							}

						}

					}
					// else if (done && !finish &&
					// list.get(0).equalsIgnoreCase("nested block join")) {

					clk++;
					if (clk <= set_clk)
						state();
				}
				// sim.write_out(outputfile);
				// running done..........
				// System.out.println("we are in the cylce");
				System.out.println(clk);
				System.out.println(set_clk);
				f = false;
				if (!f)
					refresh = 0;
				server.replyState(state(), refresh);
				// System.out.println("we are in the cylce huhu");
				// System.out.println(clk);
				// System.out.println(set_clk);

				while ((req = server.nextRequest(0)) == 'I' || req == 'X')
					;
				if (req == 'N') {
					refresh = 0;
					set_clk++;
				}
				if (req == 'C') {
					refresh = 0;
					set_clk = conflist.size();
				}
				if (req == 'F') {
					refresh = 0;
					set_clk = set_clk + conflist.size() * 2 + real_input.size()
							+ 5;
					// set_clk =set_clk+5;
				} else if (req == 'P')
					refresh = 1;
				f = false;
			}
		}
		// bingyi's Sep 6th
		else {
			sim.seperate_file();
			sim.supposedoutput();
			writer = new PrintWriter("logfile", "UTF-8");

			System.out.println("The graphic interface is disabled...");

			// bingyi need to fix
			while (!finish) {
				statetofile();
				if (clk > conflist.size() + 1 && !finish
						&& !list.get(0).equalsIgnoreCase("sorter")) {
					// System.out.println("haha, we are in nested block join, not finish");
					if (done) {
						finish = true;
					}
					if (!sim.pipeline[0].buffer_empty()) {
						if (list.get(0).equalsIgnoreCase("nested block join")) {
							System.out.println(real_input);
							sim.pipeline[0].lookaside_both(real_input,
									search_table0, search_table1, out_table);
							finish = false;
						} else {
							//System.out.println("we should get the output");
							out_table.add(sim.pipeline[0]
									.get_buffer_one(bandwidth));
						}

					}
					if (real_input.size() != 0) {
						finish = false;
					}
					int end = -1;
					for (int j = 1; j < conflist.size(); j++) {
						if (!sim.pipeline[j].buffer_empty()) {
							end = j;
						}
					}
					// System.out.println("haha end is here");
					// System.out.println(end);
					if (end >= 0) {
						finish = false;
						// for (int j=0; j<end; j++) {
						for (int j = 0; j < conflist.size() - 1; j++) {
							// if (sim.pipeline[j].buffer_empty()) {
							int rm_counter = 0;
							rm_counter = sim.pipeline[j].push_reverse(
									sim.pipeline[j + 1].get_buffer(),
									bandwidth, buf_size, rm_counter);
							sim.pipeline[j + 1].set_buf_em(rm_counter);

							// }
						}
						// System.out.printf("we are checking the %d pipeline",
						// end);
						sim.pipeline[conflist.size() - 1].check_stall(buf_size);
					}
				}
				// while (clk<=set_clk) {
				if (clk < conflist.size() + 1) {
					// if (clk < 2 && list.get(0).equalsIgnoreCase("sorter")) {
					// sim.seperate();
					// }
					sim.configure(conflist, clk);
				}
				// else if (clk < input_data.size() + conflist.size()*2+2) {
				else if (!done) {
					
					boolean all = true;

					if (list.get(0).equalsIgnoreCase("sorter")) {
						if (!sim.pipeline[conflist.size() - 1].get_lf()
								|| !sim.pipeline[conflist.size() - 1].get_rf()) {
							done = true;
						}
					} else {
						if (clk > conflist.size() + real_input.size()) {
							// System.out.println("trying to setup done");
							for (int g = 0; g < conflist.size(); g++) {
								if (!sim.pipeline[g].get_rf()
										|| sim.pipeline[g].get_tmp0f()
										|| real_input.size() != 0) {
									all = false;
								}
							}
							if (all) {
								done = true;
							}
						}
					}

					// bingyi's new code
					stop = false;
					for (int fl = 0; fl < conflist.size(); fl++) {
						if (sim.pipeline[fl].get_stall()) {
							stop = true;
						}
					}
					if (stop) {
						stallcounter++;
					}
					if (!stop) {
						// System.out.println("gosh, the input size is ");
						// System.out.println(real_input);
						if (real_input.size() != 0) {
							// System.out.println("haha, we are in bingyi's new code");
							// sim.pipeline[0].clock_move(input_data.get(0));
							// input_data.remove(0);
							//System.out.println(clk);
							if (real_input.get(0)
									.get(real_input.get(0).size() - 1)
									.equals("true")) {
								// System.out.println("in the get input stage");
								// ArrayList ex_input = new ArrayList();
								real_input.get(0).remove(
										real_input.get(0).size() - 1);
								if (list.get(0).equalsIgnoreCase("join or")
										|| list.get(0).equalsIgnoreCase(
												"join and")
										|| list.get(0).equalsIgnoreCase(
												"parallel processing")) {
									sim.pipeline[0].clock_move_parallel(
											real_input.get(0), buf_size);
								} else {
									sim.pipeline[0].clock_move(
											real_input.get(0), buf_size);
								}
								// System.out.println(real_input.get(0));
								real_input.remove(0);
							} else {
								sim.pipeline[0].last_clk_move(buf_size);
							}

						} else {
							sim.pipeline[0].last_clk_move(buf_size);
						}

						for (int j = 1; j < conflist.size(); j++) {
							if (pipeline[j - 1].out_f()) {
								// System.out.printf("the %d has a valid output\n",
								// j - 1);
								if (list.get(0).equalsIgnoreCase("join or")
										|| list.get(0).equalsIgnoreCase(
												"parallel processing")
										|| list.get(0).equalsIgnoreCase(
												"join and")) {
									pipeline[j]
											.clock_move_parallel(
													pipeline[j - 1].get_out(),
													buf_size);
								} else {
									pipeline[j]
											.clock_move(
													pipeline[j - 1].get_out(),
													buf_size);
								}
							} else {
								pipeline[j].last_clk_move(buf_size);
							}
						}
					}
				} else if (!alldone && list.get(0).equalsIgnoreCase("sorter")) {
					for (int j = 0; j < conflist.size(); j++) {
						sim.pipeline[j].push_to_buffer(j);
					}
					alldone = true;
					
				} else if (alldone && !finish
						&& list.get(0).equalsIgnoreCase("sorter")) {
					// System.out.println("test here or not");
					finish = true;
					if (!sim.pipeline[0].buffer_empty()) {
						sim.pipeline[0].lookaside(search_table0, out_table);
						finish = false;
					}
					int end = -1;
					for (int j = 1; j < conflist.size(); j++) {
						if (!sim.pipeline[j].buffer_empty()) {
							end = j;
						}
					}
					// System.out.println("haha end is here");
					// System.out.println(end);
					if (end >= 0) {
						finish = false;
						for (int j = 0; j < end; j++) {
							if (sim.pipeline[j].buffer_empty()) {
								int rm_counter = 0;
								rm_counter = sim.pipeline[j].push_reverse(
										sim.pipeline[j + 1].get_buffer(),
										bandwidth, buf_size, rm_counter);
								sim.pipeline[j + 1].set_buf_em(rm_counter);

							}
						}

					}

				}
				// else if (done && !finish &&
				// list.get(0).equalsIgnoreCase("nested block join")) {

				clk++;
				// System.out.printf("clk is %d\n", clk);
			}
			// bingyi need to fix
			if (finish) {
				PrintWriter writer2 = new PrintWriter("output", "UTF-8");
				writer2.printf(
						"Finished at cycle %d, and the output is as following\n",
						clk);
				//System.out.printf("out_table size is %d\n", out_table.size());
				for (int i =0; i<out_table.size(); i++) {
					if (list.get(0).equalsIgnoreCase("sorter")) {
					for (int j=1; j<out_table.get(i).size(); j++) {
						//System.out.print("haha");
						//System.out.print(out_table.get(i).get(j));
						writer2.print(out_table.get(i).get(j));
						writer2.print(" ");
					}
					}
					else {
					//	for (int j=0; j<out_table.get(i).size(); j++) {
						//	System.out.print("haha");
						//	System.out.print(out_table.get(i).get(j));
							//writer2.print(out_table.get(i).get(j));
						writer2.print(out_table.get(i).toString().substring(1,out_table.get(i).toString().length()-1));
							writer2.print(" ");
						//}
					}
					writer2.printf("\n");
				}
				writer2.close();
				System.out
						.printf("It took %d cycle to finish \nThe stall happens for %d\n",
								clk, stallcounter);
				if (search_table0.size() > 0 && search_table1.size() > 0) {
					System.out
							.printf("The size of search table 0 and search table 1 is %d and %d\n",
									search_table0.size()
											* search_table0.get(0).size(),
									search_table1.size()
											* search_table1.get(0).size());
				} else if (search_table0.size() == 0
						&& search_table1.size() > 0) {
					System.out.printf("The size of search table 1 is %d \n",
							search_table1.size() * search_table1.get(0).size());
				} else if (search_table1.size() == 0
						&& search_table0.size() > 0) {
					System.out.printf("The size of search table 0 is %d \n",
							search_table0.size() * search_table0.get(0).size());
				} else {
					System.out.println("we don't need a search table here");
				}
				int area_unit = 0;
				if (pipeline[0].get_left().size() != 0
						&& pipeline[0].get_right().size() != 0) {
					area_unit = pipeline[0].get_left().size()
							+ pipeline[0].get_right().size() + 2 + buf_size
							* pipeline[0].get_bufferlength() + 2
							* pipeline[0].get_tmpsize();
					// System.out.printf("the left size is %d\n",
					// pipeline[0].get_left().size());
					// System.out.printf("the right size is %d\n",
					// pipeline[0].get_right().size());
					// System.out.printf("the buffer length is %d\n",
					// pipeline[0].get_bufferlength());
					// System.out.printf("the tmp and output size is %d\n",
					// pipeline[0].get_tmpsize());
					System.out.printf("the area in each unit is %d\n",
							area_unit);
				} else if (pipeline[0].get_left().size() != 0) {
					area_unit = 2 * pipeline[0].get_left().size() + 2
							+ buf_size * pipeline[0].get_bufferlength() + 2
							* pipeline[0].get_tmpsize();
					System.out.println("it should be only sorter");
				} else if (pipeline[0].get_right().size() != 0) {
					area_unit = 2 * pipeline[0].get_right().size() + 2
							+ buf_size * pipeline[0].get_bufferlength() + 2
							* pipeline[0].get_tmpsize();
					System.out.println("it should be only sorter");
				}
				System.out.printf("the area in each unit is %d\n", area_unit);
				System.out.printf("the total area in the pipeline is %d\n",
						area_unit * conflist.size());
				double throughput = (double) (input_data.size() * Math.max(
						pipeline[0].get_left().size(), pipeline[0].get_right()
								.size()))
						/ clk;
				System.out.printf("the input data length is %d\n",
						input_data.size());
				System.out.printf("the record input size is %d\n", Math.max(
						pipeline[0].get_left().size(), pipeline[0].get_right()
								.size()));
				System.out.printf("the througput is %f", throughput);
				
			}
			writer.close();
		}

	}

	private static ArrayList<File> directoryFiles(String path, String extension) {
		ArrayList<File> allFiles = new ArrayList<File>();
		allFiles.add(new File(path));
		int index = 0;
		while (index != allFiles.size()) {
			File currentFile = allFiles.get(index);
			if (currentFile.isDirectory()) {
				allFiles.remove(index);
				for (File newFile : currentFile.listFiles())
					allFiles.add(newFile);
			} else if (!currentFile.getPath().endsWith(extension))
				allFiles.remove(index);
			else
				index++;
		}
		return allFiles;
	}

	private static String[] COMPONENTS = { "Left_R", "Left", "flow_mode",
			"Comparator_mode", "Satisfied_mode", "Unsatisfied_mode",
			"Column Selector", "selector", "position" };

	private static String[] COMPONENTS_SORT = { "Left_R", "Left", "flow_mode",
			"Comparator_mode", "Satisfied_mode", "Unsatisfied_mode",
			"Column Selector", "position" };

	private static String[] COMPONENTS_COLORS = { "purple", "yellow",
			"yellowgreen", "orange", "crimson", "khaki", "darkred",
			"palegreen", "indianred" };

	// "yellowgreen", "greenyellow", "darkgreen", "indianred"};

	private static void inputToHtml(StringBuffer buf) {
		if (!list.get(0).equalsIgnoreCase("nested block join")) {
			buf.append("<div>");
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Input Data" + "</div>\n");
			for (int c = 0; c < input_data.size(); c++) {
				String color = "white";
				for (int f = 0; f < input_data.get(c).size(); f++) {
					String cname = input_data.get(c).get(f).toString();
					buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname + "</div>\n");
				}
				buf.append("    <div style=\"clear:both;\"></div>\n");
			}
			buf.append("   </div>\n");
		} else {
			buf.append("<div>");
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Input Data" + "</div>\n");
			for (int c = 0; c < input_data0.size(); c++) {
				String color = "white";
				for (int f = 0; f < input_data0.get(c).size(); f++) {
					String cname = input_data0.get(c).get(f).toString();
					buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname + "</div>\n");
				}
				buf.append("    <div style=\"clear:both;\"></div>\n");
			}
			buf.append("   </div>\n");
		}
	}

	private static void ComponentToHtml(StringBuffer buf) {
		buf.append("<div>");
		buf.append("<div style=\"width: 1000px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
				+ "Component image" + "</div>\n");
		for (int c = 0; c < conflist.size(); c++) {
			String color = "purple";
			String cname = "l";
			buf.append("<div style=\"width: 30px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + cname + "</div>\n");
			color = "yellow";
			cname = "left";
			buf.append("<div style=\"width: 100px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + cname + "</div>\n");
			buf.append("    <div style=\"width: 340px; height: 80px; float:left;\"></div>\n");

		}
		buf.append("    <div style=\"clear:both;\"></div>\n");
		buf.append("    <div style=\"clear:both;\"></div>\n");

		for (int c = 0; c < conflist.size(); c++) {
			String color = "green";
			String cname = "f";
			buf.append("    <div style=\"width: 130px; height: 80px; float:left;\"></div>\n");
			buf.append("<div style=\"width: 20px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + cname + "</div>\n");
			// buf.append("    <div style=\"width: 500px; height: 80px; float:left;\"></div>\n");
			color = "pink";
			cname = "tmp0";
			buf.append("<div style=\"width: 100px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + cname + "</div>\n");
			buf.append("    <div style=\"width: 50px; height: 40px; float:left;\"></div>\n");
			// buf.append("    <div style=\"width: 240px; height: 80px; float:left;\"></div>\n");
			color = "red";
			cname = "f";
			// buf.append("    <div style=\"width: 400px; height: 80px; float:left;\"></div>\n");

			buf.append("<div style=\"width: 20px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + cname + "</div>\n");

			color = "yellowgreen";
			cname = "out";
			buf.append("<div style=\"width: 100px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + cname + "</div>\n");
			buf.append("    <div style=\"width: 50px; height: 80px; float:left;\"></div>\n");
		}
		buf.append("    <div style=\"clear:both;\"></div>\n");
		for (int c = 0; c < conflist.size(); c++) {
			String color = "khaki";
			String cname = "r";
			buf.append("<div style=\"width: 30px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + cname + "</div>\n");

			color = "orange";
			cname = "right";
			buf.append("<div style=\"width: 100px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + cname + "</div>\n");
			buf.append("    <div style=\"width: 340px; height: 80px; float:left;\"></div>\n");
		}
		// buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
		// + "darkred" + "\">" + "left" + "</div>\n");
		buf.append("    <div style=\"clear:both;\"></div>\n");

		for (int c = 0; c < conflist.size(); c++) {
			// private static String[] COMPONENTS_COLORS = { "red", "yellow",
			// "purple",
			// "brown", "crimson", "khaki", "darkred", "palegreen",
			// "yellowgreen"};

			// "yellowgreen", "greenyellow", "darkgreen", "indianred"};
			String color = "crimson";
			String name = "Buffer";
			buf.append("    <div style=\"width: 200px; height: 80px; float:left;\"></div>\n");
			buf.append("<div style=\"width: 200px; height: 80px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + name + "</div>\n");
			buf.append("    <div style=\"width: 80px; height: 80px; float:left;\"></div>\n");
		}
		// buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
		// + "darkred" + "\">" + "left" + "</div>\n");
		// buf.append("    <div style=\"width: 340px; height: 80px; float:left;\"></div>\n");
		buf.append("    <div style=\"clear:both;\"></div>\n");
	}

	// bingyi gui false state
	private static void statetofile() {

		if (clk < conflist.size() + 1) {
			if (clk == 0)
				writer.println("Cycle " + clk + " Ready to start!");
			else {
				writer.printf("Cycle " + clk
						+ ": pushing the configuration file ");
				for (int i = clk - 1; i > -1; i--) {
					writer.print(conflist.get(i) + " ");
				}
				writer.print("\n");
			}
		} else {
			writer.println("Cycle " + clk + " :");
			for (int c = 0; c < conflist.size(); c++) {
				if (pipeline[c].get_lchange()) {
					writer.print("==>");
				} else {
					writer.print("   ");
				}
				if (pipeline[c].get_lf()) {
					writer.print("empty[]");

					if (!pipeline[c].get_stall()) {
						writer.print("    ");
					} else {
						writer.print("stall");
					}

				} else {
					writer.print(pipeline[c].get_left());
					;
					if (!pipeline[c].get_stall()) {
						writer.print("    ");
					} else {
						writer.print(" stall");
					}
				}
				writer.print("								");
			}
			writer.println();

			for (int c = 0; c < conflist.size(); c++) {
				writer.print("					");

				boolean cname = pipeline[c].get_tmp0f();

				if (pipeline[c].get_tchange()) {
					writer.print("==>");
				} else {
					writer.print("   ");
				}
				writer.print(cname);
				writer.print(pipeline[c].get_tmp0());
				writer.print("	");
				if (pipeline[c].get_ochange()) {
					buf.append("<div style=\"width: 60px; height: 20px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid white; background-color: "
							+ "white" + "\">" + "==>" + "</div>\n");
				} else {
					buf.append("    <div style=\"width: 60px; height: 20px; float:left;\"></div>\n");
				}
				// color = "yellow";
				cname = pipeline[c].get_outf();
				writer.print(cname);
				writer.print(" ");
				writer.print(pipeline[c].get_out_new());
				writer.print("		");
			}
			writer.println();

			for (int c = 0; c < conflist.size(); c++) {
				if (pipeline[c].get_rchange()) {
					writer.print("==>");
				} else {
					writer.print("   ");
				}
				if (pipeline[c].get_rf()) {
					writer.print("empty[]");
				} else {
					writer.print(pipeline[c].get_rightr());
					writer.print(pipeline[c].get_right());
				}
				writer.print("									");
			}
			writer.println();

			for (int c = 0; c < conflist.size(); c++) {
				writer.print("				");
				if (pipeline[c].get_bchange()) {
					writer.print("==>");
				} else {
					writer.print("   ");
				}
				writer.print(pipeline[c].get_buffer());

				if (pipeline[c].get_brchange()) {
					writer.print("<==");
				} else {
					buf.append("    ");
				}
				writer.print("			");
			}
			writer.println();
		}
	}

	// bingyi gui false state
	private static void StatetoHtml(StringBuffer buf) {
		buf.append("    <div style=\"clear:both;\"></div>\n");
		buf.append("<div>");
		// buf.append("<div style=\"width: 1000px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
		// + " We are hereCycle " + clk + "</div>\n");

		for (int c = 0; c < conflist.size(); c++) {

			if (pipeline[c].get_lchange()) {
				buf.append("<div style=\"width: 60px; height: 20px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid white; background-color: "
						+ "white" + "\">" + "==>" + "</div>\n");
			} else {
				buf.append("    <div style=\"width: 60px; height: 20px; float:left;\"></div>\n");
			}
			if (pipeline[c].get_lf()) {
				String color = "purple";
				String cname = "empty";

				buf.append("<div style=\"width: 30px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname + "</div>\n");
				color = "yellow";
				String cname0 = "[]";
				buf.append("<div style=\"width: 100px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname0 + "</div>\n");
				if (!pipeline[c].get_stall()) {
					buf.append("    <div style=\"width: 340px; height: 80px; float:left;\"></div>\n");
				} else {
					color = "red";
					String cname_tmp = "stall";
					buf.append("<div style=\"width: 100px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname_tmp + "</div>\n");
					buf.append("    <div style=\"width: 240px; height: 80px; float:left;\"></div>\n");
				}

			} else {
				String color = "purple";
				int cname = pipeline[c].get_leftr();

				buf.append("<div style=\"width: 30px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname + "</div>\n");
				color = "yellow";
				ArrayList cname0 = pipeline[c].get_left();
				buf.append("<div style=\"width: 100px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname0 + "</div>\n");
				// buf.append("    <div style=\"width: 340px; height: 80px; float:left;\"></div>\n");
				if (!pipeline[c].get_stall()) {
					buf.append("    <div style=\"width: 340px; height: 80px; float:left;\"></div>\n");
				} else {
					color = "red";
					String cname_tmp = "stall";
					buf.append("<div style=\"width: 100px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
							+ color + "\">" + cname_tmp + "</div>\n");
					buf.append("    <div style=\"width: 240px; height: 80px; float:left;\"></div>\n");
				}
			}
		}
		buf.append("    <div style=\"clear:both;\"></div>\n");
		buf.append("    <div style=\"clear:both;\"></div>\n");

		for (int c = 0; c < conflist.size(); c++) {

			String color;
			boolean cname = pipeline[c].get_tmp0f();
			String ctmp;
			if (cname) {
				ctmp = "T";
				color = "green";
			} else {
				ctmp = "F";
				color = "red";
			}
			buf.append("    <div style=\"width: 130px; height: 80px; float:left;\"></div>\n");
			if (pipeline[c].get_tchange()) {
				buf.append("<div style=\"width: 60px; height: 20px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid white; background-color: "
						+ "white" + "\">" + "==>" + "</div>\n");
			} else {
				buf.append("    <div style=\"width: 60px; height: 20px; float:left;\"></div>\n");
			}
			buf.append("<div style=\"width: 20px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + ctmp + "</div>\n");
			// buf.append("    <div style=\"width: 500px; height: 80px; float:left;\"></div>\n");
			color = "pink";
			ArrayList cname0 = pipeline[c].get_tmp0();
			buf.append("<div style=\"width: 100px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + cname0 + "</div>\n");
			// buf.append("    <div style=\"width: 50px; height: 40px; float:left;\"></div>\n");
			// buf.append("    <div style=\"width: 240px; height: 80px; float:left;\"></div>\n");
			if (pipeline[c].get_ochange()) {
				buf.append("<div style=\"width: 60px; height: 20px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid white; background-color: "
						+ "white" + "\">" + "==>" + "</div>\n");
			} else {
				buf.append("    <div style=\"width: 60px; height: 20px; float:left;\"></div>\n");
			}
			// color = "yellow";
			cname = pipeline[c].get_outf();

			if (cname) {
				ctmp = "T";
				color = "green";
			} else {
				ctmp = "F";
				color = "red";
			}
			// buf.append("    <div style=\"width: 400px; height: 80px; float:left;\"></div>\n");

			buf.append("<div style=\"width: 20px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + ctmp + "</div>\n");

			color = "yellowgreen";
			cname0 = pipeline[c].get_out_new();
			buf.append("<div style=\"width: 100px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + cname0 + "</div>\n");
			buf.append("    <div style=\"width: 50px; height: 80px; float:left;\"></div>\n");
		}
		buf.append("    <div style=\"clear:both;\"></div>\n");
		for (int c = 0; c < conflist.size(); c++) {
			if (pipeline[c].get_rchange()) {
				buf.append("<div style=\"width: 60px; height: 20px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid white; background-color: "
						+ "white" + "\">" + "==>" + "</div>\n");
			} else {
				buf.append("    <div style=\"width: 60px; height: 20px; float:left;\"></div>\n");
			}
			if (pipeline[c].get_rf()) {
				String color = "khaki";
				String cname = "empty";
				buf.append("<div style=\"width: 30px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname + "</div>\n");

				color = "orange";
				String cname0 = "[]";
				buf.append("<div style=\"width: 100px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname0 + "</div>\n");
				buf.append("    <div style=\"width: 340px; height: 80px; float:left;\"></div>\n");
			} else {
				String color = "khaki";
				int cname = pipeline[c].get_rightr();
				buf.append("<div style=\"width: 30px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname + "</div>\n");

				color = "orange";
				ArrayList cname0 = pipeline[c].get_right();
				buf.append("<div style=\"width: 100px; height: 40px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname0 + "</div>\n");
				buf.append("    <div style=\"width: 340px; height: 80px; float:left;\"></div>\n");
			}
		}
		buf.append("    <div style=\"clear:both;\"></div>\n");

		for (int c = 0; c < conflist.size(); c++) {
			String color = "crimson";
			ArrayList name = pipeline[c].get_buffer();
			buf.append("    <div style=\"width: 200px; height: 80px; float:left;\"></div>\n");
			// if (!pipeline[c].get_brchange()&&pipeline[c].get_bchange()) {
			if (pipeline[c].get_bchange()) {
				buf.append("<div style=\"width: 30px; height: 20px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid white; background-color: "
						+ "white" + "\">" + "==>" + "</div>\n");
			} else {
				buf.append("    <div style=\"width: 30px; height: 20px; float:left;\"></div>\n");
			}
			buf.append("<div style=\"width: 170px; height: 80px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
					+ color + "\">" + name + "</div>\n");
			buf.append("    <div style=\"width: 80px; height: 80px; float:left;\"></div>\n");
			if (pipeline[c].get_brchange()) {
				buf.append("<div style=\"width: 30px; height: 20px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid white; background-color: "
						+ "white" + "\">" + "<==" + "</div>\n");
			} else {
				buf.append("    <div style=\"width: 30px; height: 20px; float:left;\"></div>\n");
			}
		}
	}

	private static void ConfToHtml(StringBuffer buf) {
		buf.append("<div>");
		buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
				+ "Simulator Configuration Setup" + "</div>\n");
		buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
				+ "buf_size" + buf_size + "</div>\n");
		buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
				+ "bandwidth" + bandwidth + "</div>\n");
		buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
				+ "look_buf_size" + look_buf_size + "</div>\n");
		for (int i = 0; i < list.size(); i++) {
			buf.append("<div style=\"width: 800px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ list.get(i) + "</div>\n");
		}
		buf.append("    <div style=\"width: 0px; height: 40px; float:left;\"></div>\n");
		buf.append("    <div style=\"clear:both;\"></div>\n");
		for (int c = 0; c < conflist.get(0).size(); c++) {
			if (!list.get(0).equalsIgnoreCase("sorter")) {
				String color = COMPONENTS_COLORS[c];
				String cname = COMPONENTS[c];
				buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname + "</div>\n");
			} else {
				String color = COMPONENTS_COLORS[c];
				String cname = COMPONENTS_SORT[c];
				buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname + "</div>\n");
			}
		}
		buf.append("    <div style=\"clear:both;\"></div>\n");
		for (int f = 0; f < conflist.size(); f++) {
			for (int c = 0; c < conflist.get(f).size(); c++) {
				String color = COMPONENTS_COLORS[c];
				String cname = (String) conflist.get(f).get(c);
				buf.append("<div style=\"width: 200px; height: 40px; font-size:20px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid black; background-color: "
						+ color + "\">" + cname + "</div>\n");
			}
			buf.append("    <div style=\"clear:both;\"></div>\n");
		}
		buf.append("   <div style=\"clear:both;\"></div>\n");
		buf.append("    </div>\n");
		buf.append("    <div style=\"clear:both;\"></div>\n");
		buf.append("</div>");
	}

	private static String state() {
		int pixels = 5000;
		String title = "Simulator";
		buf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
		buf.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-US\" xml:lang=\"en\">\n");
		buf.append("<head>\n");
		buf.append(" <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-7\" />\n");
		buf.append(" <link rel=\"shortcut icon\" href=\"cell/icon.ico\" />\n");
		buf.append(" <title>" + title + "</title>\n");
		buf.append(" <style type=\"text/css\">\n");
		buf.append("  a:link {text-decoration: none; color: blue;}\n");
		buf.append("  a:visited {text-decoration: none; color: blue;}\n");
		buf.append("  a:hover {text-decoration: none; color: red;}\n");
		buf.append("  a:active {text-decoration: none; color: blue;}\n");
		buf.append(" </style>\n");
		buf.append("</head>\n");
		buf.append("<body>\n");
		// general part
		buf.append(" <div style=\"width:" + pixels
				+ "px; margin-left:auto; margin-right: auto;\">\n");
		// left part
		buf.append("  <div style=\"width: 5000px; float: left;\">\n");
		if (clk == 0) {
			// System.out.println("following we should seperate buf");
			ConfToHtml(buf);
			buf.append("   <div style=\"width: 600px; height: 50px;\"></div>\n");
			buf.append("   <div style=\"clear:both;\"></div>\n");
			inputToHtml(buf);
			seperate(buf);
			ComponentToHtml(buf);
		}
		buf.append("<div style=\"width: 200px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
				+ "Clock Cycle " + clk + "</div>\n");
		if (stop) {
			buf.append("<div style=\"width: 1000px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Stall happens!!" + "</div>\n");
		}
		if (clk == conflist.size()) {
			buf.append("<div style=\"width: 1000px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Arrive the correct location!!" + "</div>\n");
		}
		if (clk == conflist.size() + 1) {
			buf.append("<div style=\"width: 1000px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Set all the components!!" + "</div>\n");
		}
		if (!done) {
			ConfiguretoHtml(buf);
			if (clk == conflist.size() + 1) {
				for (int i = 0; i < conflist.size(); i++) {
					buf.append("<div style=\"width: 460px; height: 20px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid white; background-color: "
							+ "white" + "\">" + "||" + "</div>\n");
				}
			}
			buf.append("   <div style=\"clear:both;\"></div>\n");
			if (clk == conflist.size() + 1) {
				for (int i = 0; i < conflist.size(); i++) {
					buf.append("<div style=\"width: 460px; height: 20px; font-size:12px; font-weight:bold;font-family:'Comic Sans MS', cursive, sans-serif;text-align:center;float:left; border: 1px solid white; background-color: "
							+ "white" + "\">" + "V" + "</div>\n");
				}
			}
		}
		if (clk > conflist.size() + 1 && !done) {
			buf.append("<div style=\"width: 1000px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Data processing" + "</div>\n");
			StatetoHtml(buf);
			if (list.get(0).equalsIgnoreCase("nested block join")) {
				output(buf);
			}
			if (list.get(0).equalsIgnoreCase("parallel processing")) {
				output(buf);
			}
		} else if (done && !finish && list.get(0).equals("sorter")) {
			System.out.println(conflist.get(0));
			buf.append("<div style=\"width: 2000px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Sorter Done and Start LookingAside Stage." + "</div>\n");
			StatetoHtml(buf);
			System.out.println("haha, we should be here, srt");
			output(buf);
		}

		else if (finish) {
			buf.append("<div style=\"width: 2000px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
					+ "Finally done!" + "</div>\n");
			output(buf);
		} else {
			// System.out.println(list.get(0));
			if (clk > conflist.size() + 1) {
				buf.append("<div style=\"width: 2000px; height: 40px; text-align: center;font-size: 25px; font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">"
						+ "Waiting for the input data to enter the look aside buffer"
						+ "</div>\n");
				StatetoHtml(buf);
				// System.out.println("haha, we should be here, srt");
				output(buf);
			}
		}
		// space above
		buf.append("   <div style=\"width: 600px; height: 50px;\"></div>\n");
		buf.append("   <div style=\"clear:both;\"></div>\n");
		// space between buttons and array
		buf.append("   <div style=\"width: 200px; height: 50px; float:left;\"></div>\n");
		buf.append("   <div style=\"clear:both;\"></div>\n");
		buf.append("   <div style=\"width: 400px; height: 70px; float:left; cursor: pointer; text-align: center; font-size: 40px;\n");
		buf.append("               font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\"><a href=\"step\">Clock</a></div>\n");
		buf.append("  </div>\n");
		buf.append("   <div style=\"width: 400px; height: 70px; float:left; cursor: pointer; text-align: center; font-size: 40px;\n");
		buf.append("               font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\"><a href=\"conf\">Configure</a></div>\n");
		buf.append("  </div>\n");
		buf.append("  </div>\n");
		buf.append("   <div style=\"width: 400px; height: 70px; float:left; cursor: pointer; text-align: center; font-size: 40px;\n");
		buf.append("               font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\"><a href=\"finish\">Process</a></div>\n");
		buf.append("  </div>\n");
		buf.append("   <div style=\"width: 600px; height: 50px;\"></div>\n");
		buf.append("   <div style=\"clear:both;\"></div>\n");
		buf.append("   <div style=\"width: 600px; height: 50px;\"></div>\n");
		buf.append("   <div style=\"clear:both;\"></div>\n");
		buf.append("</div");
		buf.append(" </div>\n");
		buf.append("</body>\n");
		buf.append("</html>\n");
		return buf.toString();

	}
}