import java.util.ArrayList;
import java.util.Arrays;

public class comparator {
	
	private boolean stall;
	
	private int column_selector;

	
	private ArrayList left = new ArrayList();

	private ArrayList right = new ArrayList();

	private ArrayList sat_out = new ArrayList();

	private ArrayList unsat_out = new ArrayList();

	private ArrayList output = new ArrayList();

	private ArrayList tmp0 = new ArrayList();

	private ArrayList<ArrayList> buffer = new ArrayList<ArrayList>();

	private ArrayList join = new ArrayList();

	private int left_register, right_register, buffer_length, tmp_size;
	
	// sat_out, unsat_out, output, tmp0;

	private boolean left_empty, right_empty, sat_f, unsat_f, out_valid,
			tmp0_valid, left_change, right_change, tmp0_change, out_change,
			buffer_change, end_signal, buffer_reverse_change, stall_change;

	private enum reg_mod {
		one_fix, non_fix
	}

	private reg_mod register_mod;

	private enum comp_mod {
		larger, smaller, equal_w
	}

	private comp_mod comparator_mod;

	private enum satisfied_mod {
		keepon, enter_buffer, keepon_enter, keepon_enter_last
	}

	private satisfied_mod sa_mod;

	private enum unsatisfied_mod {
		discard, keepon, keepon_last
	}

	private unsatisfied_mod unsa_mod;

	public comparator(int left_r, ArrayList left_d, String r_mod, String c_mod,
			String s_mod, String u_mod, ArrayList jn, int cs) {
		//System.out.println("the left register");
		//System.out.println(left_d);
		this.register_mod = reg_mod.valueOf(r_mod);
		this.comparator_mod = comp_mod.valueOf(c_mod);
		this.sa_mod = satisfied_mod.valueOf(s_mod);
		this.unsa_mod = unsatisfied_mod.valueOf(u_mod);
		this.out_valid = false;
		this.tmp0_valid = false;
		this.sat_f = false;
		this.unsat_f = false;
		this.left_change = false;
		this.right_change = false;
		this.tmp0_change = false;
		this.end_signal = false;
		this.stall = false;
		this.stall_change = false;
		this.join = jn;
		if (this.register_mod.equals(register_mod.one_fix)) {
			this.left_register = left_r;
			this.left = left_d;
			this.right_empty = true;
		} else if (this.register_mod.equals(register_mod.non_fix)) {
			this.left_empty = true;
			this.right_empty = true;
		}
		this.column_selector = cs;
	}

	public void clock_move(ArrayList line, int buf_size) {
		
		this.out_change = false;
		this.complete();
		this.left_change = false;
		this.right_change = false;
		this.tmp0_change = false;
		this.buffer_change = false;
		this.buffer_reverse_change = false;
		if (this.register_mod.equals(register_mod.non_fix)) {
			clock_move_nonfix(line);
		} else if (this.register_mod.equals(register_mod.one_fix)) {
			clock_move_onefix(line, buf_size);
		}
		//System.out.println(this.buffer);
	}
public void clock_move_parallel(ArrayList line, int buf_size) {
		
		this.out_change = false;
		this.complete();
		this.left_change = false;
		this.right_change = false;
		this.tmp0_change = false;
		this.buffer_change = false;
		this.buffer_reverse_change = false;
		if (this.register_mod.equals(register_mod.non_fix)) {
			clock_move_nonfix(line);
		} else if (this.register_mod.equals(register_mod.one_fix)) {
			clock_move_onefix_parallel(line, buf_size);
		}
		
		
	}
	public boolean get_stall(){
		return this.stall;
	}

	public void last_clk_move(int buf_size) {
		this.out_change = false;
		this.complete();
		this.left_change = false;
		this.right_change = false;
		this.tmp0_change = false;
		this.buffer_change = false;
		if (this.register_mod.equals(register_mod.non_fix)) {
			clock_last_nonfix();
		} else if (this.register_mod.equals(register_mod.one_fix)) {
			clock_last_onefix(buf_size);
		}
		//System.out.println(this.buffer);
	}

	public void output_disable() {
		this.out_valid = false;
	}

	public boolean end_signal() {
		return this.end_signal;
	}

	public void clock_move_nonfix(ArrayList line) {
		if (!this.left_empty && !this.right_empty) {
			if (this.right_empty == false) {
				if (this.comparator_mod.equals(comparator_mod.larger)) {
					this.push_large();
				}
				if (this.comparator_mod.equals(comparator_mod.smaller)) {
					this.push_small();
				}
			}
		}
		if (this.left_empty == true) {
			this.left_empty = false;
			this.left = line;
			this.left_change = true;
			//System.out.println(line.get(this.column_selector));
			left_register = Integer.parseInt((String) line.get(this.column_selector));

		} else if (this.right_empty) {
			this.right_empty = false;
			this.right_change = true;
			this.right = line;
			this.right_register = Integer.parseInt((String) line.get(this.column_selector));
		}
	}

	public void clock_last_nonfix() {
		if (!this.left_empty && !this.right_empty) {
			if (this.right_empty == false) {
				if (this.comparator_mod.equals(comparator_mod.larger)) {
					this.push_large();
				}
				if (this.comparator_mod.equals(comparator_mod.smaller)) {
					this.push_small();
				}
			}
		}

	}

	public void push_large() {
		if (this.left_register > this.right_register) {
			this.left_empty = true;
			this.tmp0_valid = true;
			this.tmp0_change = true;
			this.tmp0 = this.left;
		} else if (this.left_register <= this.right_register) {
			this.right_empty = true;
			this.tmp0_valid = true;
			this.tmp0_change = true;
			this.tmp0 = this.right;
			if (tmp_size ==0) {
				tmp_size = tmp0.size();
			}
		}
	}

	public void push_small() {
		if (this.left_register < this.right_register) {
			this.left_empty = true;
			this.tmp0_valid = true;
			this.tmp0_change = true;
			this.tmp0 = this.left;
		} else if (this.left_register >= this.right_register) {
			this.right_empty = true;
			this.tmp0_valid = true;
			this.tmp0_change = true;
			this.tmp0 = this.right;
		}
		if (tmp_size ==0) {
			tmp_size = tmp0.size();
		}
	}

	public void clock_move_onefix(ArrayList line, int buf_size) {
		
		this.sat_f = false;
		this.unsat_f = false;
		this.tmp0_valid = false;
		this.right_change = true;
		if (!this.right_empty) {
			//System.out.println("we are here");
			this.compare();
			this.after_compare(buf_size);
		}
		this.right_empty = false;
		this.right = line;
		//System.out.println(line);
		//this.right_register = (Integer) line.get(this.column_selector);
		
		this.right_register = Integer.parseInt((String) line.get(this.column_selector));
		//this.right_register = (Integer) line.get(this.column_selector);
		
	}

public void clock_move_onefix_parallel(ArrayList line, int buf_size) {
		
		this.sat_f = false;
		this.unsat_f = false;
		this.tmp0_valid = false;
		this.right_change = true;
		if (!this.right_empty) {
			//System.out.println("we are here");
			this.compare();
			this.after_compare(buf_size);
		}
		this.right_empty = false;
		this.right = line;
		//System.out.println(line);
		//this.right_register = (Integer) line.get(this.column_selector);
		
		//this.right_register = Integer.parseInt((String) line.get(this.column_selector));
		this.right_register = (Integer) line.get(this.column_selector);
		
	}
	public void clock_last_onefix(int buf_size) {
		this.sat_f = false;
		this.unsat_f = false;
		this.tmp0_valid = false;
		this.right_change = false;
		if (!this.left_empty && !this.right_empty) {

			this.compare();
			this.after_compare(buf_size);
			this.right_empty = true;
		}
		
	}

	public void compare() {
		if (this.comparator_mod.equals(comparator_mod.larger)) {
			if (this.left_register < this.right_register) {
				this.sat_f = true;
				this.sat_out = this.right;
			} else {
				this.unsat_f = true;
				this.unsat_out = this.right;
			}
		} else if (this.comparator_mod.equals(comparator_mod.smaller)) {
			if (this.left_register > this.right_register) {
				this.sat_f = true;
				this.sat_out = this.right;
			} else {
				this.unsat_f = true;
				this.unsat_out = this.right;
			}
		} else if (this.comparator_mod.equals(comparator_mod.equal_w)) {
			if (this.left_register == this.right_register) {
				this.sat_f = true;
				this.sat_out = this.right;
			} else {
				this.unsat_f = true;
				this.unsat_out = this.right;
			}
		}
	}

	public void after_compare(int buf_size) {
		if (this.sat_f == true) {
			if ((this.sa_mod.equals(sa_mod.keepon_enter)||this.sa_mod.equals(sa_mod.keepon_enter_last)||this.sa_mod.equals(sa_mod.enter_buffer))&& this.buffer.size()>=buf_size) {
				this.stall = true;
				this.tmp0_change = true;
				this.tmp0_valid = true;
				this.tmp0 = this.sat_out;
				if (this.sa_mod.equals(sa_mod.keepon_enter_last)) {
					this.tmp0.add("end");
				}
				if (tmp_size==0) {
					tmp_size = tmp0.size();
				}
			}
			else {
			if (this.sa_mod.equals(sa_mod.keepon)
					|| this.sa_mod.equals(sa_mod.keepon_enter)) {
				this.tmp0_change = true;
				this.tmp0_valid = true;
				this.tmp0 = this.sat_out;
				if (tmp_size==0) {
					tmp_size = tmp0.size();
				}
			}
			if (this.sa_mod.equals(sa_mod.enter_buffer)
					|| this.sa_mod.equals(sa_mod.keepon_enter)||this.sa_mod.equals(sa_mod.keepon_enter_last)) {
				if (this.sat_f) {
					if (!buffer.contains(this.sat_out))
						this.buffer_change = true;
					ArrayList ha = new ArrayList();
					//System.out.println(this.join.size());
					if (this.join.size()!=0) {
						ha.add(this.left.get(0));
						for (int q = 0; q < join.size(); q++) {
							ha.add(this.sat_out.get(Integer.parseInt((String) join.get(q))));
						}
						//buffer.add(ha);
						//System.out.println("we are here!!!");
					}
					else {
					if (this.left.size()>0){
						ha.add(this.left.get(0));
						
					}
					ha.add(this.sat_out.get(0));
					}
					if (this.sa_mod.equals(sa_mod.enter_buffer)) {
						//ha.add("end");
					}
					if (this.sa_mod.equals(sa_mod.keepon_enter_last)) {
						ha.add("end");
					}
					if (ha.size()>buffer_length) {
						buffer_length=ha.size();
					}
					//if (ha.size()<2) {
					//System.out.println(ha);
					//System.out.println("the ha is not right here");
				//	}
					buffer.add(ha);
				}
				
			}
		}
		} 
		if (this.unsat_f == true && this.unsa_mod.equals(unsa_mod.keepon)) {
			this.tmp0_valid = true;
			this.tmp0 = this.unsat_out;
			this.tmp0_change = true;
		}
		if (this.unsat_f == true && this.unsa_mod.equals(unsa_mod.keepon_last)) {
			
				if (this.sa_mod.equals(sa_mod.keepon_enter_last) && this.buffer.size()>=buf_size) {
					this.stall = true;
					this.tmp0_change = true;
					this.tmp0_valid = true;
					this.tmp0 = this.unsat_out;
					this.tmp0.add("no_end");
				}
				else {
			if (!buffer.contains(this.sat_out))
				this.buffer_change = true;
			ArrayList ha = new ArrayList();
			ha.add(this.left.get(0));
			ha.add(this.unsat_out.get(0));
			ha.add("no_end");
			//System.out.println(ha);
			buffer.add(ha);
				}
		}
	}
	public int get_tmpsize() {
		return this.tmp_size;
	}
	public int get_bufferlength() {
		return buffer_length;
	}

	public String get_name() {
		String tmp = "";
		tmp = tmp + "(";
		tmp = tmp + this.register_mod;
		tmp = tmp + ", ";
		tmp = tmp + this.comparator_mod;
		if (this.left_register != 0) {
			tmp = tmp + this.left_register;
		}
		tmp = tmp + ", ";
		tmp = tmp + this.sa_mod;
		tmp = tmp + ", ";
		tmp = tmp + this.unsa_mod;
		tmp = tmp + ")==========";
		return tmp;
	}

	public boolean get_lf() {
		return this.left_empty;
	}

	public boolean get_rf() {
		return this.right_empty;
	}

	public void get_register() {
		if (this.register_mod.equals(register_mod.non_fix)) {
			if (this.left_empty) {
				System.out.format("%d ", this.right_register);
			} else {
				System.out.format("%d ", this.left_register);
			}
		}
	}

	public boolean get_lchange() {
		return this.left_change;
	}

	public boolean get_rchange() {
		return this.right_change;
	}

	public ArrayList get_out() {
		this.out_valid = false;
		return this.output;
	}

	public boolean out_f() {
		return this.out_valid;
	}

	public String get_sort_state(StringBuffer tmp) {
		if (!this.left_empty && this.left_register != 0) {
			tmp.append("left= ");
			tmp.append(this.left_register);
			tmp.append("left array=");
			tmp.append(this.left);
		}
		if (!this.right_empty && this.right_register != 0) {
			tmp.append(" right= ");
			tmp.append(this.right_register);
			tmp.append("right_array");
			tmp.append(this.right);
		}
		tmp.append("\n");
		return tmp.toString();

	}
	
	public void push_to_buffer(int i) {
		if (this.left_empty && !this.right_empty) {
			this.buffer_change = true;
			this.right.add(i);
			this.buffer.add(this.right);
			this.right_empty = true;
		}
		else if (this.right_empty && !this.left_empty) {
			this.buffer_change = true;
			this.buffer.add(this.left);
			this.left.add(i);
			this.left_empty = true;
			
		}
	}

	public boolean buffer_empty() {
		if (this.buffer.size()==0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void lookaside(ArrayList<ArrayList> look_table, ArrayList<ArrayList> out_table) {
		if (!this.buffer_empty()) {
			out_table.add(look_table.get((Integer) (this.buffer.get(0).get(0))));
			this.buffer_reverse_change = true;
			this.buffer.remove(0);
		}
	}
	public void lookaside_both(ArrayList<ArrayList> real_input, ArrayList<ArrayList> look_table, ArrayList<ArrayList> look_table0, ArrayList<ArrayList> out_table) {
		if (!this.buffer_empty()) {
			String tmp;
			if (this.buffer.get(0).get(this.buffer.get(0).size()-1).equals("no_end")){
				look_table0.get((Integer) this.buffer.get(0).get(1)).remove(look_table0.get((Integer) this.buffer.get(0).get(1)).size()-1);
				look_table0.get((Integer) this.buffer.get(0).get(1)).add("done");
				for (int i=0; i<real_input.size(); i++) {
					if (real_input.get(i).get(real_input.get(i).size()-1).equals("false")) {
						//System.out.println("we are in the try to change boolean stage");
						//System.out.println(real_input);
						real_input.get(i).remove(real_input.get(i).size()-1);
						real_input.get(i).add("true");
						//System.out.println(real_input);
						break;
					}
				}
				for (int i=0; i<look_table0.size(); i++) {
					if (look_table0.get(i).get(look_table0.get(i).size()-1).equals("false")) {
						look_table0.get(i).remove(look_table0.get(i).size()-1);
						look_table0.get(i).add("true");
						break;
					}
				}
				//real_input.get((Integer) this.buffer.get(0).get(1)).remove(real_input.get((Integer) this.buffer.get(0).get(1)).size()-1);
				//real_input.get((Integer) this.buffer.get(0).get(1)).add("done");
			}
			else {
			//	System.out.println(look_table.get((Integer.parseInt( (String) this.buffer.get(0).get(0)))).toString());
				//System.out.println(look_table.get((Integer.parseInt( (String) this.buffer.get(0).get(0)))).toString().substring(3));
				//StringBuilder stringBuilder = new StringBuilder();
				ArrayList tmpout = new ArrayList();
				for (int i=1; i<look_table.get((Integer.parseInt( (String) this.buffer.get(0).get(0)))).size(); i++) {
					tmpout.add(look_table.get((Integer.parseInt( (String) this.buffer.get(0).get(0)))).get(i));
				}
			//tmp = look_table.get((Integer.parseInt( (String) this.buffer.get(0).get(0)))).toString().substring(3);
			//tmp = tmp.substring(0, tmp.length()-1);
			//tmp = tmp+ look_table0.get((Integer) this.buffer.get(0).get(1)).toString().substring(2);
			for (int i=1; i<look_table0.get((Integer) this.buffer.get(0).get(1)).size()-1; i++) {
				tmpout.add(look_table0.get((Integer) this.buffer.get(0).get(1)).get(i));
				
			}
		//	java.util.List<String> item2 = new ArrayList();
			//item2 = Arrays.asList(tmp
				//	.substring(1, tmp.length() - 1)
					//.split(","));
			//ArrayList array_tmp0 = new ArrayList();
			//for (int f = 0; f < item2.size()-1; f++) {
				//array_tmp0.add(item2.get(f));
			//}
			
			//out_table.add(array_tmp0);
			out_table.add(tmpout);
			}
			if (this.buffer.get(0).get(this.buffer.get(0).size()-1).equals("end")){
				look_table0.get((Integer) this.buffer.get(0).get(1)).remove(look_table0.get((Integer) this.buffer.get(0).get(1)).size()-1);
				look_table0.get((Integer) this.buffer.get(0).get(1)).add("false");
				for (int i=0; i<real_input.size(); i++) {
					if (real_input.get(i).get(real_input.get(i).size()-1).equals("false")) {
						real_input.get(i).remove(real_input.get(i).size()-1);
						real_input.get(i).add("true");
					}
				}
				//real_input.get((Integer) this.buffer.get(0).get(1)).remove(look_table0.get((Integer) this.buffer.get(0).get(1)).size()-1);
				//real_input.get((Integer) this.buffer.get(0).get(1)).remove(look_table0.get((Integer) this.buffer.get(0).get(1)).size()-1);
				//real_input.get((Integer) this.buffer.get(0).get(1)).add("false");
			}
			this.buffer_reverse_change = true;
			this.buffer.remove(0);
			
		}
	}
	
	public void set_buf_em(int band_width) {
		//this.buffer.clear();
		//this.buffer.remove(0);
		if (this.buffer.size()<band_width) {
			this.buffer.clear();
		}
		else {
			for (int i=0; i<band_width; i++) {
				this.buffer.remove(0);
				
			}
			
		}
	}
	
	public boolean get_brchange() {
		return this.buffer_reverse_change;
	}
	public void check_stall(int buf_size) {
		if (this.stall== true && this.buffer.size()<buf_size && this.sat_f == true) {
			this.stall = false;
			this.stall_change = true;
			this.buffer_change = true;
			ArrayList ha = new ArrayList();
			//ha.add(this.left.get(0));
			//ha.add(this.tmp0.get(0));
			
			if (this.join.size()!=0) {
				ha.add(this.left.get(0));
				for (int q = 0; q < join.size(); q++) {
					ha.add(this.sat_out.get(Integer.parseInt((String) join.get(q))));
				}
				//buffer.add(ha);
				//System.out.println("we are here!!!");
			}
			//bingyi's new else
			else {
				ha.add(this.left.get(0));
				ha.add(this.tmp0.get(0));
			}
			if (this.sa_mod.equals(sa_mod.keepon_enter)) {
				buffer.add(ha);
			}	
			else if (this.sa_mod.equals(sa_mod.keepon_enter_last)) {
				ha.add(this.tmp0.get(this.tmp0.size()-1));
				buffer.add(ha);
			}
		}
	}
	
	public int push_reverse(ArrayList<ArrayList> buf, int band_width, int buf_size, int counter) {
		if (buf.size() !=0){
		//this.buffer.clear();
		if (buf.size()<band_width) {
			if (buf.size()+1+this.buffer.size()<= buf_size) {
			counter = buf.size();
			for (int i=0; i<buf.size(); i++) {
				this.buffer.add(buf.get(i));
				this.buffer_reverse_change = true;
			}
			}
			else {
				//System.out.println("in the else part haha");
				if (buf_size-1-this.buffer.size() > 0) {
					counter = buf_size-1-this.buffer.size();
				for (int i=0; i<counter; i++) {
					this.buffer.add(buf.get(i));
					this.buffer_reverse_change = true;
				}
				}
			}
		}
		else {
			if (band_width+1+this.buffer.size()<=buf_size) {
				counter = band_width;
			for (int i=0; i<band_width; i++){
			this.buffer.add(buf.get(i));
			//this.buffer.add(buf.get(1));
			this.buffer_reverse_change = true;
			}
			}
			else {
				//System.out.println("in second the else part haha");
				if (buf_size-1-this.buffer.size()>0) {
					counter = buf_size-1-this.buffer.size();
					for (int i=0; i<counter; i++) {
						this.buffer.add(buf.get(i));
						this.buffer_reverse_change = true;
					}
				}
			}
		}
		}
		this.stall_change = false;
		
		if (this.buffer.size() < buf_size) {
			if (this.stall == true){
				//System.out.println("haha, the stall is changing!!!");
			this.stall = false;
			this.stall_change = true;
				this.buffer_change = true;
				ArrayList ha = new ArrayList();
				ha.add(this.left.get(0));
				ha.add(this.tmp0.get(0));
				buffer.add(ha);
			}
			this.stall = false;
			}
		
		return counter;
		
	}
	
	public String get_parallel_state(StringBuffer tmp) {
		if (!this.left_empty && this.left_register != 0) {
			tmp.append(this.column_selector);
			tmp.append(this.comparator_mod);
			tmp.append(this.left_register);

		}
		tmp.append("buffer is:");
		tmp.append("[");
		for (int i = 0; i < this.buffer.size(); i++) {
			tmp.append(this.buffer.get(i));
		}
		tmp.append("]");
		tmp.append("\n");
		return tmp.toString();

	}

	public String get_joinand_state(StringBuffer tmp) {

		tmp.append("buffer is:");
		tmp.append("[");
		for (int i = 0; i < this.buffer.size(); i++) {
			tmp.append(this.buffer.get(i));
		}
		tmp.append("]");
		tmp.append("\n");
		return tmp.toString();

	}

	public String get_joinor_state(StringBuffer tmp) {
		if (!this.left_empty && this.left_register != 0) {
			tmp.append(this.column_selector);
			tmp.append(this.comparator_mod);
			tmp.append(this.left_register);

		}
		tmp.append("buffer is:");
		tmp.append("[");
		for (int i = 0; i < this.buffer.size(); i++) {
			tmp.append(this.buffer.get(i));
		}
		tmp.append("]");
		tmp.append("\n");
		return tmp.toString();

	}

	public String get_nested_state(StringBuffer tmp) {
		if (!this.left_empty && this.left_register != 0) {
			tmp.append("left array=");
			tmp.append(this.left);
		} else {
			tmp.append("left = empty, ");
		}
		tmp.append("buffer is:");
		tmp.append("[");
		for (int i = 0; i < this.buffer.size(); i++) {
			tmp.append(this.buffer.get(i));
		}
		tmp.append("]");
		tmp.append("\n");
		return tmp.toString();

	}

	public String get_state(StringBuffer tmp) {
		if (!this.left_empty && this.left_register != 0) {
			tmp.append("[");
			tmp.append(this.left_register);
			tmp.append(", ");
			tmp.append(this.left);
		} else {
			tmp.append("[empty, empty, ");
		}
		if (!this.right_empty && this.right_register != 0) {
			// tmp.append(" right= ");
			tmp.append(this.right_register);
			tmp.append(", ");
			tmp.append(this.right);
			tmp.append(", ");
		} else {
			tmp.append(", empty, empty, ");
		}
		if (this.tmp0_valid) {
			// tmp.append(" tmp0= ");
			tmp.append(this.tmp0);
			tmp.append(", ");
		} else {
			tmp.append("invalid, ");
		}
		if (this.out_valid) {
			// tmp.append(" output= ");
			tmp.append(this.output);
			tmp.append(", ");
		} else {
			tmp.append("invalid, ");
		}
		tmp.append("[");
		for (int i = 0; i < this.buffer.size(); i++) {
			tmp.append(this.buffer.get(i));
		}
		tmp.append("]");
		tmp.append("<br>");
		return tmp.toString();

	}

	public boolean get_tchange() {
		return this.tmp0_change;
	}

	public boolean get_ochange() {
		return this.out_change;
	}

	public boolean get_bchange() {
		return this.buffer_change;
	}

	public int get_leftr() {
		return this.left_register;
	}

	public ArrayList get_left() {
		return this.left;
	}

	public int get_rightr() {
		return this.right_register;
	}

	public ArrayList get_right() {
		return this.right;
	}

	public boolean get_tmp0f() {
		return this.tmp0_valid;
	}

	public boolean get_outf() {
		return this.out_valid;
	}

	public ArrayList get_tmp0() {
		return this.tmp0;
	}

	public ArrayList get_out_new() {
		return this.output;
	}

	public ArrayList get_buffer() {
		return this.buffer;
	}
	public ArrayList get_buffer_one(int bandwidth) {
		ArrayList<ArrayList> tmpu = new ArrayList<ArrayList>();
		for (int i=0; i<Math.min(bandwidth, this.buffer.size()); i++) {
		ArrayList tmp = new ArrayList();
		tmp = this.buffer.get(0);
		this.buffer.remove(0);
		tmpu.add(tmp);
		}
		return tmpu;
	}
	public void get_status() {
		if (!this.left_empty) {
			System.out.format("[%d, ", this.left_register);

			System.out.print(this.left);
			System.out.print(", ");
		} else {
			System.out.print("[empty, ");
		}
		if (!this.right_empty) {
			System.out.format("%d, ", this.right_register);
			System.out.print(this.right);
			System.out.print(", ");
		} else {
			System.out.print("empty, ");
		}
		if (this.tmp0_valid) {

			System.out.print(this.tmp0);
			System.out.print(", ");

		} else {
			System.out.format("invalid, ");
		}
		if (this.out_valid) {
			System.out.print(this.output);
			System.out.print(", ");
		} else {
			System.out.format("invalid, ");
		}
		System.out.print(this.buffer);
		System.out.print("]");
	}

	public void complete() {

		if (this.tmp0_valid) {
			this.tmp0_valid = false;
			this.out_valid = true;
			this.out_change = true;
			this.output = this.tmp0;
		}
	}

}