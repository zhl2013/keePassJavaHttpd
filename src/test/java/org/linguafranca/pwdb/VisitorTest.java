/*
 * Copyright 2015 Jo Rabin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.linguafranca.pwdb;

import org.junit.Test;
import org.linguafranca.pwdb.kdbx.dom.DomDatabaseWrapper;
import org.linguafranca.pwdb.Entry.Matcher;
import org.linguafranca.pwdb.kdbx.KdbxCredentials;
import org.linguafranca.pwdb.kdbx.KdbxStreamFormat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * @author Jo
 */
public class VisitorTest {
	List<Entry> visitorList = new ArrayList<>();
	Visitor visitor = new Visitor() {
		StringBuffer indentation = new StringBuffer();

		@Override
		public void startVisit(Group group) {
			System.out.println(indentation.toString() + group.getName());
			indentation.append("   ");
		}

		@Override
		public void endVisit(Group group) {
			indentation.setLength(indentation.length() - 3);
		}

		@Override
		public boolean isEntriesFirst() {
			return false;
		}

		@Override
		public void visit(Entry entry) {
			System.out.println(indentation.toString() + "= " + entry.getTitle());
			visitorList.add(entry);
		}
	};

	@Test
	public void testLoadDB() {
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test123.kdbx");
			DomDatabaseWrapper db = new DomDatabaseWrapper(new KdbxStreamFormat(),
					new KdbxCredentials.Password("123".getBytes()), inputStream);
			db.visit(visitor);

			List<Entry> matched = db.findEntries(new Entry.Matcher() {
				String matchee = "";

				@Override
				public boolean matches(Entry entry) {
					return entry.getTitle().toLowerCase().contains(matchee)
							|| entry.getNotes().toLowerCase().contains(matchee)
							|| entry.getUsername().toLowerCase().contains(matchee);
				}
			});

			assertTrue(matched.size() == visitorList.size());

			for (Entry e : matched) {
				assertTrue(visitorList.contains(e));
			}
		} catch (Exception e) {
			assertTrue("Couldn\'t open test DB " + e.getMessage(), false);
		}
	}

	@Test
    public void testMyKdbx(){
		try {
			String dbName = "/Users/zhanghl/.keepass/mydata.kdbx";
			InputStream inputStream = new FileInputStream(dbName);
			DomDatabaseWrapper db;
			String keyfile = "/Users/zhanghl/.Trash/myKey";
			InputStream keyinputStream = new FileInputStream(keyfile );
			db = new DomDatabaseWrapper(new KdbxStreamFormat(), new KdbxCredentials.KeyFile("110120".getBytes(), keyinputStream ), inputStream);
//			db.visit(visitor);
			
			List<Entry> matched = db.findEntries(new Entry.Matcher() {
				String matchee = "社保";

				@Override
				public boolean matches(Entry entry) {
					return entry.getTitle().toLowerCase().contains(matchee)
							|| entry.getNotes().toLowerCase().contains(matchee)
							|| entry.getUsername().toLowerCase().contains(matchee);
				}
			});

			for (Entry e : matched) {
				System.out.println(e.getPassword() + "|" + e.getUsername());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
