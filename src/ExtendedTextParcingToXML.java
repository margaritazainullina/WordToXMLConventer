import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

public class ExtendedTextParcingToXML {
	public static void main(String[] args) {
		// Парсится текстовый файл, где строка имеет формат "箒\tほうき\tbroom "
		// и сохраняется в xml формата:
		// <card>
		// <word wordId=1546>箒</word>
		// <meanings>
		// <meaning transcription="ほうき">
		// <statistics status="0"/>
		// <translations>
		// <word>broom</word>
		// </translations>
		// <examples/>
		// </meaning>
		// </meanings>
		// </card>
		// для уровней от 5 до 1 по отдельности и 5+4, 5+4+3 и т.д.

		// извлекаем записи из файла в массивы строк типа [箒,ほうき,broom]
		String[][] entries1 = fetch("C://proj//XMLMaking//src//JLPT_txt//1.txt");
		String[][] entries2 = fetch("C://proj//XMLMaking//src//JLPT_txt//2.txt");
		String[][] entries3 = fetch("C://proj//XMLMaking//src//JLPT_txt//3.txt");
		String[][] entries4 = fetch("C://proj//XMLMaking//src//JLPT_txt//4.txt");
		String[][] entries5 = fetch("C://proj//XMLMaking//src//JLPT_txt//5.txt");

		// сливаем массивы для получения общих списков слов
		// 5+4
		String[][] entries54 = new String[entries5.length + entries4.length][3];
		System.arraycopy(entries5, 0, entries54, 0, entries5.length);
		System.arraycopy(entries4, 0, entries54, entries5.length,
				entries4.length);

		// 5+4+3
		String[][] entries543 = new String[entries54.length + entries3.length][3];
		System.arraycopy(entries54, 0, entries543, 0, entries54.length);
		System.arraycopy(entries3, 0, entries543, entries54.length,
				entries3.length);

		// 5+4+3+2
		String[][] entries5432 = new String[entries543.length + entries2.length][3];
		System.arraycopy(entries543, 0, entries5432, 0, entries543.length);
		System.arraycopy(entries2, 0, entries5432, entries543.length,
				entries2.length);
		// 1+2+3+4+5
		String[][] entries54321 = new String[entries5432.length
				+ entries1.length][3];
		System.arraycopy(entries5432, 0, entries54321, 0, entries5432.length);
		System.arraycopy(entries1, 0, entries54321, entries5432.length,
				entries1.length);

		// компаратор для сортировки по алфавиту (по столбцу с хираганой)
		final Comparator<String[]> arrayComparator = new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String[] o2) {
				try {
					return o1[1].compareTo(o2[1]);
				} catch (Exception e) {
					return 0;
				}
			}
		};

		// собственно сортировка
		Arrays.sort(entries54, arrayComparator);
		Arrays.sort(entries543, arrayComparator);
		Arrays.sort(entries5432, arrayComparator);
		Arrays.sort(entries54321, arrayComparator);

		// превращаем наши массивы в XML
		arrayToXML(entries1, "C://proj//XMLMaking//src//JLPT_txt//JLPT_N1.xml",
				"JLPT N1 (doesn't contain the vocab needed by JLPT N1 and below");
		arrayToXML(entries2, "C://proj//XMLMaking//src//JLPT_txt//JLPT_N2.xml",
				"JLPT N2 (doesn't contain the vocab needed by JLPT N1 and below");
		arrayToXML(entries3, "C://proj//XMLMaking//src//JLPT_txt//JLPT_N3.xml",
				"JLPT N3 (doesn't contain the vocab needed by JLPT N1 and below");
		arrayToXML(entries4, "C://proj//XMLMaking//src//JLPT_txt//JLPT_N4.xml",
				"JLPT N4 (doesn't contain the vocab needed by JLPT N1 and below");
		arrayToXML(entries5, "C://proj//XMLMaking//src//JLPT_txt//JLPT_N5.xml",
				"JLPT N5");
		arrayToXML(entries54,
				"C://proj//XMLMaking//src//JLPT_txt//JLPT_N4_extended.xml",
				"JLPT N4");
		arrayToXML(entries543,
				"C://proj//XMLMaking//src//JLPT_txt//JLPT_N3_extended.xml",
				"JLPT N3");
		arrayToXML(entries5432,
				"C://proj//XMLMaking//src//JLPT_txt//JLPT_N2_extended.xml",
				"JLPT N2");
		arrayToXML(entries54321,
				"C://proj//XMLMaking//src//JLPT_txt//JLPT_N1_extended.xml",
				"JLPT N1");
	}

	public static void arrayToXML(String[][] arr, String outputPath,
			String title) {
		// шапка xml
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<dictionary xmlns=\"\" formatVersion=\"4\" title=\""
				+ title
				+ "\" sourceLanguageId=\"1041\" "
				+ "destinationLanguageId=\"1033\" nextWordId=\"0\">"
				+ "<statistics defectiveMeaningsQuantity=\"0\" suspendedMeaningsQuantity=\"0\" readyMeaningsQuantity=\"0\" "
				+ "activeMeaningsQuantity=\"0\" repeatedMeaningsQuantity=\"0\" learnedMeaningsQuantity=\"0\"/>");

		// wordId
		int id = 0;

		// создаем элемент card для каждой записи с подэлементами
		for (final String[] entry : arr) {
			try {
				// парсим запись
				id++;
				StringBuilder card = new StringBuilder();
				card.append("<card>");
				String[] meanings = entry[2].split(";");

				card.append("<word wordId=" + id + ">" + entry[0] + "</word>");
				card.append("<meanings>");
				card.append("<meaning transcription=\"" + entry[1] + "\">");
				card.append("<statistics status=\"0\"/><translations>");

				// записываем переводы слова
				for (String meaning : meanings) {
					card.append("<word>" + meaning.trim() + "</word>");
				}

				card.append("</translations>");
				card.append("<examples/>");
				card.append("</meaning>");
				card.append("</meanings>");
				card.append("</card>");
				xml.append(card);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// записываем в файл UTF-8 для японского, UTF-16 для китайского
		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputPath), "UTF-8"))) {
			out.write(xml.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// извлекаем записи из файла
	// метод принимает строку к входному файлу, получаем двумерный массив
	// записей вида [箒,ほうき,broom]
	public static String[][] fetch(String inputPath) {
		int i = 0;
		int num = 0;

		// вычисляем количество записей
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputPath), "UTF-8"))) {
			while (buffer.readLine() != null)
				num++;
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[][] entries = new String[num][3];

		// извлекаем их
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputPath), "UTF-8"))) {
			String entry;

			while ((entry = buffer.readLine()) != null) {
				// 3 столбца разделены знаками табуляции
				String[] parts = entry.split("\t");
				entries[i][0] = parts[0].trim();
				entries[i][1] = parts[1].trim();
				entries[i][2] = parts[2].trim();
				i++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return entries;
	}
}
