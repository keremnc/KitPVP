package net.frozenorb.KitPVP.DataSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.frozenorb.KitPVP.API.KitAPI;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public abstract class DataManager {
	private File file;
	private BasicDBObject data;

	public DataManager(File f) {
		this.file = f;
		if (!f.exists()) {
			KitAPI.getKitPVP().saveResource(f.getName(), false);
		}
		loadData();
		saveData();

	}

	public final BasicDBObject getData() {
		return data;
	}

	public final void setData(BasicDBObject data) {
		this.data = data;
	}

	public void loadData() {
		BufferedReader br = null;

		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(file));
			StringBuilder json = new StringBuilder();
			while ((sCurrentLine = br.readLine()) != null) {
				json.append(sCurrentLine);
			}
			data = (BasicDBObject) JSON.parse(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			onLoad();
		}
	}

	public void saveData() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(data.toString());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public abstract void onLoad();
}
