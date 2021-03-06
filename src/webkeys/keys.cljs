;; Copyright © 2016 Dynamic Object Language Labs Inc.
;;
;; This software is licensed under the terms of the
;; Apache License, Version 2.0 which can be found in
;; the file LICENSE at the root of this distribution.

(ns webkeys.keys
  "Cross browser key event mangement in ClojureScript"
  (:require [clojure.string :as string]
            [avenir.utils :refer [xor]]))

(def SHIFT_KEY 16)
(def CTRL_KEY 17)
(def ALT_KEY 18)
(def CAPS_LOCK 20)
(def META_KEY 91)

(def SHIFT_KEY_NAME "Shift")
(def CTRL_KEY_NAME "Control")
(def ALT_KEY_NAME "Alt")
(def CAPS_LOCK_NAME "CapsLock")
(def META_KEY_NAME "Meta")

(def SHIFT_OFFSET 256)
(def CTRL_OFFSET 512)
(def ALT_OFFSET 1024)
(def META_OFFSET 2048)

(def extra-keys-initial
  {SHIFT_KEY_NAME false ;; :shift-key
   CTRL_KEY_NAME false ;; :ctrl-key
   ALT_KEY_NAME false ;; :alt-key
   CAPS_LOCK_NAME false ;; :caps-lock
   ;; Disable Meta for now (problematic on Mac OS X)
   ;; META_KEY_NAME false ;; :meta-key
   })

(def extra-keys-set (set (keys extra-keys-initial)))

(def extra-keys (atom extra-keys-initial))

(defn reset-extra-keys! []
  (reset! extra-keys extra-keys-initial))

(defn shift? []
  (let [ek @extra-keys]
    (xor (get ek SHIFT_KEY_NAME) (get ek CAPS_LOCK_NAME))))

(defn extra-keys-text []
  (let [ek @extra-keys
        shift (if (xor (get ek SHIFT_KEY_NAME) (get ek CAPS_LOCK_NAME)) "Shift ")
        ctrl (if (get ek CTRL_KEY_NAME) "Control ")
        alt  (if (get ek ALT_KEY_NAME) "Alt ")
        metak (if (get ek META_KEY_NAME) "Meta ")]
    (str metak (if metak " ")
      alt (if alt " ")
      ctrl (if ctrl " ")
      shift (if shift " "))))

(def no-shift-prefix
  #{"0" "1" "2" "3" "4" "5" "6" "7" "8" "9" "10"
    "a" "b" "c" "d" "e" "f" "g" "h" "i" "j" "k" "l" "m"
    "n" "o" "p" "q" "r" "s" "t" "u" "v" "w" "x" "y" "z"
    "A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K" "L" "M"
    "N" "O" "P" "Q" "R" "S" "T" "U" "V" "W" "X" "Y" "Z"
    ";" "=" "-" "," "." "/" "`" "[" "\\" "]"
    "'" "!" "@" "#" "$" "%" "^" "&" "*" "(" ")"
    ":" "+" "<" "_" ">" "?" "~" "{" "|" "}" "\""
    })

(defn needs-shift-prefix? [key]
  (not (no-shift-prefix key)))

(defn extra-keys-prefix [key & [ek]]
  (if-not (extra-keys-set key)
    (let [ek (or ek @extra-keys)
          shift (if (and (xor (get ek SHIFT_KEY_NAME) (get ek CAPS_LOCK_NAME))
                      (needs-shift-prefix? key))
                  "S-")
          ctrl (if (get ek CTRL_KEY_NAME) "C-")
          alt  (if (get ek ALT_KEY_NAME) "A-")
          metak (if (get ek META_KEY) "M-")]
      (str metak alt ctrl shift))))

(defn register-key-fn [id key-fn]
  (swap! extra-keys assoc id key-fn))

(def key-translation
  {0 "Unidentified",
   8 "Backspace",
   9 "Tab",
   12 "Clear",
   13 "Enter",
   SHIFT_KEY SHIFT_KEY_NAME,
   CTRL_KEY CTRL_KEY_NAME,
   ALT_KEY ALT_KEY_NAME,
   19 "Pause",
   CAPS_LOCK CAPS_LOCK_NAME,
   27 "Escape",
   32 " ",
   33 "PageUp",
   34 "PageDown",
   35 "End",
   36 "Home",
   37 "ArrowLeft",
   38 "ArrowUp",
   39 "ArrowRight",
   40 "ArrowDown",
   42 "PrintScreen",
   45 "Insert",
   46 "Delete",
   48 "0",
   49 "1",
   50 "2",
   51 "3",
   52 "4",
   53 "5",
   54 "6",
   55 "7",
   56 "8",
   57 "9",
   59 ";",
   61 "=",
   65 "a",
   66 "b",
   67 "c",
   68 "d",
   69 "e",
   70 "f",
   71 "g",
   72 "h",
   73 "i",
   74 "j",
   75 "k",
   76 "l",
   77 "m",
   78 "n",
   79 "o",
   80 "p",
   81 "q",
   82 "r",
   83 "s",
   84 "t",
   85 "u",
   86 "v",
   87 "w",
   88 "x",
   89 "y",
   90 "z",
   META_KEY META_KEY_NAME, ;; Windows, OS
   112 "F1",
   113 "F2",
   114 "F3",
   115 "F4",
   116 "F5",
   117 "F6",
   118 "F7",
   119 "F8",
   120 "F9",
   121 "F10",
   122 "F11",
   123 "F12",
   173 "-",
   186 ";",
   188 ",",
   189 "-",
   190 ".",
   191 "/",
   192 "`",
   219 "[",
   220 "\\",
   221 "]",
   222 "'",
   264 "S-Backspace",
   265 "S-Tab",
   268 "S-Clear",
   269 "S-Enter",
   272 SHIFT_KEY_NAME,
   273 CTRL_KEY_NAME,
   274 ALT_KEY_NAME,
   275 "S-Pause",
   276 CAPS_LOCK_NAME,
   283 "S-Escape",
   288 "S- ",
   289 "S-PageUp",
   290 "S-PageDown",
   291 "S-End",
   292 "S-Home",
   293 "S-ArrowLeft",
   294 "S-ArrowUp",
   295 "S-ArrowRight",
   296 "S-ArrowDown",
   298 "S-PrintScreen",
   301 "S-Insert",
   302 "S-Delete",
   304 ")",
   305 "!",
   306 "@",
   307 "#",
   308 "$",
   309 "%",
   310 "^",
   311 "&",
   312 "*",
   313 "(",
   314 ")",
   315 ":",
   317 "+",
   321 "A",
   322 "B",
   323 "C",
   324 "D",
   325 "E",
   326 "F",
   327 "G",
   328 "H",
   329 "I",
   330 "J",
   331 "K",
   332 "L",
   333 "M",
   334 "N",
   335 "O",
   336 "P",
   337 "Q",
   338 "R",
   339 "S",
   340 "T",
   341 "U",
   342 "V",
   343 "W",
   344 "X",
   345 "Y",
   346 "Z",
   347 "S-Windows",
   368 "S-F1",
   369 "S-F2",
   370 "S-F3",
   371 "S-F4",
   372 "S-F5",
   373 "S-F6",
   374 "S-F7",
   375 "S-F8",
   376 "S-F9",
   377 "S-F10",
   378 "S-F11",
   379 "S-F12",
   429 "_",
   442 ":",
   444 "<",
   445 "_",
   446 ">",
   447 "?",
   448 "~",
   475 "{",
   476 "|",
   477 "}",
   478 "\"",
   520 "C-Backspace",
   521 "C-Tab",
   524 "C-Clear",
   525 "C-Enter",
   528 SHIFT_KEY_NAME,
   529 CTRL_KEY_NAME,
   530 ALT_KEY_NAME,
   531 "C-Pause",
   532 CAPS_LOCK_NAME,
   539 "C-Escape",
   544 "C- ",
   545 "C-PageUp",
   546 "C-PageDown",
   547 "C-End",
   548 "C-Home",
   549 "C-ArrowLeft",
   550 "C-ArrowUp",
   551 "C-ArrowRight",
   552 "C-ArrowDown",
   554 "C-PrintScreen",
   557 "C-Insert",
   558 "C-Delete",
   560 "C-0",
   561 "C-1",
   562 "C-2",
   563 "C-3",
   564 "C-4",
   565 "C-5",
   566 "C-6",
   567 "C-7",
   568 "C-8",
   569 "C-9",
   571 "C-;",
   573 "C-=",
   577 "C-a",
   578 "C-b",
   579 "C-c",
   580 "C-d",
   581 "C-e",
   582 "C-f",
   583 "C-g",
   584 "C-h",
   585 "C-i",
   586 "C-j",
   587 "C-k",
   588 "C-l",
   589 "C-m",
   590 "C-n",
   591 "C-o",
   592 "C-p",
   593 "C-q",
   594 "C-r",
   595 "C-s",
   596 "C-t",
   597 "C-u",
   598 "C-v",
   599 "C-w",
   600 "C-x",
   601 "C-y",
   602 "C-z",
   603 "C-Windows",
   624 "C-F1",
   625 "C-F2",
   626 "C-F3",
   627 "C-F4",
   628 "C-F5",
   629 "C-F6",
   630 "C-F7",
   631 "C-F8",
   632 "C-F9",
   633 "C-F10",
   634 "C-F11",
   635 "C-F12",
   685 "C--",
   698 "C-;",
   700 "C-,",
   701 "C--",
   702 "C-.",
   703 "C-/",
   704 "C-`",
   731 "C-[",
   732 "C-\\",
   733 "C-]",
   734 "C-'",
   776 "C-S-Backspace",
   777 "C-S-Tab",
   780 "C-S-Clear",
   781 "C-S-Enter",
   784 SHIFT_KEY_NAME,
   785 CTRL_KEY_NAME,
   786 ALT_KEY_NAME,
   787 "C-S-Pause",
   788 CAPS_LOCK_NAME,
   795 "C-S-Escape",
   800 "C-S- ",
   801 "C-S-PageUp",
   802 "C-S-PageDown",
   803 "C-S-End",
   804 "C-S-Home",
   805 "C-S-ArrowLeft",
   806 "C-S-ArrowUp",
   807 "C-S-ArrowRight",
   808 "C-S-ArrowDown",
   810 "C-S-PrintScreen",
   813 "C-S-Insert",
   814 "C-S-Delete",
   816 "C-)",
   817 "C-!",
   818 "C-@",
   819 "C-#",
   820 "C-$",
   821 "C-%",
   822 "C-^",
   823 "C-&",
   824 "C-*",
   825 "C-(",
   826 "C-)",
   827 "C-:",
   829 "C-+",
   833 "C-A",
   834 "C-B",
   835 "C-C",
   836 "C-D",
   837 "C-E",
   838 "C-F",
   839 "C-G",
   840 "C-H",
   841 "C-I",
   842 "C-J",
   843 "C-K",
   844 "C-L",
   845 "C-M",
   846 "C-N",
   847 "C-O",
   848 "C-P",
   849 "C-Q",
   850 "C-R",
   851 "C-S",
   852 "C-T",
   853 "C-U",
   854 "C-V",
   855 "C-W",
   856 "C-X",
   857 "C-Y",
   858 "C-Z",
   859 "C-S-Windows",
   880 "C-S-F1",
   881 "C-S-F2",
   882 "C-S-F3",
   883 "C-S-F4",
   884 "C-S-F5",
   885 "C-S-F6",
   886 "C-S-F7",
   887 "C-S-F8",
   888 "C-S-F9",
   889 "C-S-F10",
   890 "C-S-F11",
   891 "C-S-F12",
   941 "C-_",
   954 "C-:",
   956 "C-<",
   957 "C-_",
   958 "C->",
   959 "C-?",
   960 "C-~",
   987 "C-{",
   988 "C-|",
   989 "C-}",
   990 "C-\"",
   1032 "A-Backspace",
   1033 "A-Tab",
   1036 "A-Clear",
   1037 "A-Enter",
   1040 SHIFT_KEY_NAME,
   1041 CTRL_KEY_NAME,
   1042 ALT_KEY_NAME,
   1043 "A-Pause",
   1044 CAPS_LOCK_NAME,
   1051 "A-Escape",
   1056 "A- ",
   1057 "A-PageUp",
   1058 "A-PageDown",
   1059 "A-End",
   1060 "A-Home",
   1061 "A-ArrowLeft",
   1062 "A-ArrowUp",
   1063 "A-ArrowRight",
   1064 "A-ArrowDown",
   1066 "A-PrintScreen",
   1069 "A-Insert",
   1070 "A-Delete",
   1072 "A-0",
   1073 "A-1",
   1074 "A-2",
   1075 "A-3",
   1076 "A-4",
   1077 "A-5",
   1078 "A-6",
   1079 "A-7",
   1080 "A-8",
   1081 "A-9",
   1083 "A-;",
   1085 "A-=",
   1089 "A-a",
   1090 "A-b",
   1091 "A-c",
   1092 "A-d",
   1093 "A-e",
   1094 "A-f",
   1095 "A-g",
   1096 "A-h",
   1097 "A-i",
   1098 "A-j",
   1099 "A-k",
   1100 "A-l",
   1101 "A-m",
   1102 "A-n",
   1103 "A-o",
   1104 "A-p",
   1105 "A-q",
   1106 "A-r",
   1107 "A-s",
   1108 "A-t",
   1109 "A-u",
   1110 "A-v",
   1111 "A-w",
   1112 "A-x",
   1113 "A-y",
   1114 "A-z",
   1115 "A-Windows",
   1136 "A-F1",
   1137 "A-F2",
   1138 "A-F3",
   1139 "A-F4",
   1140 "A-F5",
   1141 "A-F6",
   1142 "A-F7",
   1143 "A-F8",
   1144 "A-F9",
   1145 "A-F10",
   1146 "A-F11",
   1147 "A-F12",
   1197 "A--",
   1210 "A-;",
   1212 "A-,",
   1213 "A--",
   1214 "A-.",
   1215 "A-/",
   1216 "A-`",
   1243 "A-[",
   1244 "A-\\",
   1245 "A-]",
   1246 "A-'",
   1288 "A-S-Backspace",
   1289 "A-S-Tab",
   1292 "A-S-Clear",
   1293 "A-S-Enter",
   1296 SHIFT_KEY_NAME,
   1297 CTRL_KEY_NAME,
   1298 ALT_KEY_NAME,
   1299 "A-S-Pause",
   1300 CAPS_LOCK_NAME,
   1307 "A-S-Escape",
   1312 "A-S- ",
   1313 "A-S-PageUp",
   1314 "A-S-PageDown",
   1315 "A-S-End",
   1316 "A-S-Home",
   1317 "A-S-ArrowLeft",
   1318 "A-S-ArrowUp",
   1319 "A-S-ArrowRight",
   1320 "A-S-ArrowDown",
   1322 "A-S-PrintScreen",
   1325 "A-S-Insert",
   1326 "A-S-Delete",
   1328 "A-)",
   1329 "A-!",
   1330 "A-@",
   1331 "A-#",
   1332 "A-$",
   1333 "A-%",
   1334 "A-^",
   1335 "A-&",
   1336 "A-*",
   1337 "A-(",
   1338 "A-)",
   1339 "A-:",
   1341 "A-+",
   1345 "A-A",
   1346 "A-B",
   1347 "A-C",
   1348 "A-D",
   1349 "A-E",
   1350 "A-F",
   1351 "A-G",
   1352 "A-H",
   1353 "A-I",
   1354 "A-J",
   1355 "A-K",
   1356 "A-L",
   1357 "A-M",
   1358 "A-N",
   1359 "A-O",
   1360 "A-P",
   1361 "A-Q",
   1362 "A-R",
   1363 "A-S",
   1364 "A-T",
   1365 "A-U",
   1366 "A-V",
   1367 "A-W",
   1368 "A-X",
   1369 "A-Y",
   1370 "A-Z",
   1371 "A-S-Windows",
   1392 "A-S-F1",
   1393 "A-S-F2",
   1394 "A-S-F3",
   1395 "A-S-F4",
   1396 "A-S-F5",
   1397 "A-S-F6",
   1398 "A-S-F7",
   1399 "A-S-F8",
   1400 "A-S-F9",
   1401 "A-S-F10",
   1402 "A-S-F11",
   1403 "A-S-F12",
   1453 "A-_",
   1466 "A-:",
   1468 "A-<",
   1469 "A-_",
   1470 "A->",
   1471 "A-?",
   1472 "A-~",
   1499 "A-{",
   1500 "A-|",
   1501 "A-}",
   1502 "A-\"",
   1544 "A-C-Backspace",
   1545 "A-C-Tab",
   1548 "A-C-Clear",
   1549 "A-C-Enter",
   1552 SHIFT_KEY_NAME,
   1553 CTRL_KEY_NAME,
   1554 ALT_KEY_NAME,
   1555 "A-C-Pause",
   1556 CAPS_LOCK_NAME,
   1563 "A-C-Escape",
   1568 "A-C- ",
   1569 "A-C-PageUp",
   1570 "A-C-PageDown",
   1571 "A-C-End",
   1572 "A-C-Home",
   1573 "A-C-ArrowLeft",
   1574 "A-C-ArrowUp",
   1575 "A-C-ArrowRight",
   1576 "A-C-ArrowDown",
   1578 "A-C-PrintScreen",
   1581 "A-C-Insert",
   1582 "A-C-Delete",
   1584 "A-C-0",
   1585 "A-C-1",
   1586 "A-C-2",
   1587 "A-C-3",
   1588 "A-C-4",
   1589 "A-C-5",
   1590 "A-C-6",
   1591 "A-C-7",
   1592 "A-C-8",
   1593 "A-C-9",
   1595 "A-C-;",
   1597 "A-C-=",
   1601 "A-C-a",
   1602 "A-C-b",
   1603 "A-C-c",
   1604 "A-C-d",
   1605 "A-C-e",
   1606 "A-C-f",
   1607 "A-C-g",
   1608 "A-C-h",
   1609 "A-C-i",
   1610 "A-C-j",
   1611 "A-C-k",
   1612 "A-C-l",
   1613 "A-C-m",
   1614 "A-C-n",
   1615 "A-C-o",
   1616 "A-C-p",
   1617 "A-C-q",
   1618 "A-C-r",
   1619 "A-C-s",
   1620 "A-C-t",
   1621 "A-C-u",
   1622 "A-C-v",
   1623 "A-C-w",
   1624 "A-C-x",
   1625 "A-C-y",
   1626 "A-C-z",
   1627 "A-C-Windows",
   1648 "A-C-F1",
   1649 "A-C-F2",
   1650 "A-C-F3",
   1651 "A-C-F4",
   1652 "A-C-F5",
   1653 "A-C-F6",
   1654 "A-C-F7",
   1655 "A-C-F8",
   1656 "A-C-F9",
   1657 "A-C-F10",
   1658 "A-C-F11",
   1659 "A-C-F12",
   1709 "A-C--",
   1722 "A-C-;",
   1724 "A-C-,",
   1725 "A-C--",
   1726 "A-C-.",
   1727 "A-C-/",
   1728 "A-C-`",
   1755 "A-C-[",
   1756 "A-C-\\",
   1757 "A-C-]",
   1758 "A-C-'",
   1800 "A-C-S-Backspace",
   1801 "A-C-S-Tab",
   1804 "A-C-S-Clear",
   1805 "A-C-S-Enter",
   1808 SHIFT_KEY_NAME,
   1809 CTRL_KEY_NAME,
   1810 ALT_KEY_NAME,
   1811 "A-C-S-Pause",
   1812 CAPS_LOCK_NAME,
   1819 "A-C-S-Escape",
   1824 "A-C-S- ",
   1825 "A-C-S-PageUp",
   1826 "A-C-S-PageDown",
   1827 "A-C-S-End",
   1828 "A-C-S-Home",
   1829 "A-C-S-ArrowLeft",
   1830 "A-C-S-ArrowUp",
   1831 "A-C-S-ArrowRight",
   1832 "A-C-S-ArrowDown",
   1834 "A-C-S-PrintScreen",
   1837 "A-C-S-Insert",
   1838 "A-C-S-Delete",
   1840 "A-C-)",
   1841 "A-C-!",
   1842 "A-C-@",
   1843 "A-C-#",
   1844 "A-C-$",
   1845 "A-C-%",
   1846 "A-C-^",
   1847 "A-C-&",
   1848 "A-C-*",
   1849 "A-C-(",
   1850 "A-C-)",
   1851 "A-C-:",
   1853 "A-C-+",
   1857 "A-C-A",
   1858 "A-C-B",
   1859 "A-C-C",
   1860 "A-C-D",
   1861 "A-C-E",
   1862 "A-C-F",
   1863 "A-C-G",
   1864 "A-C-H",
   1865 "A-C-I",
   1866 "A-C-J",
   1867 "A-C-K",
   1868 "A-C-L",
   1869 "A-C-M",
   1870 "A-C-N",
   1871 "A-C-O",
   1872 "A-C-P",
   1873 "A-C-Q",
   1874 "A-C-R",
   1875 "A-C-S",
   1876 "A-C-T",
   1877 "A-C-U",
   1878 "A-C-V",
   1879 "A-C-W",
   1880 "A-C-X",
   1881 "A-C-Y",
   1882 "A-C-Z",
   1883 "A-C-S-Windows",
   1904 "A-C-S-F1",
   1905 "A-C-S-F2",
   1906 "A-C-S-F3",
   1907 "A-C-S-F4",
   1908 "A-C-S-F5",
   1909 "A-C-S-F6",
   1910 "A-C-S-F7",
   1911 "A-C-S-F8",
   1912 "A-C-S-F9",
   1913 "A-C-S-F10",
   1914 "A-C-S-F11",
   1915 "A-C-S-F12",
   1965 "A-C-_",
   1978 "A-C-:",
   1980 "A-C-<",
   1981 "A-C-_",
   1982 "A-C->",
   1983 "A-C-?",
   1984 "A-C-~",
   2011 "A-C-{",
   2012 "A-C-|",
   2013 "A-C-}",
   2014 "A-C-\""}
  )

(defn event->key [kc]
  (let [ek @extra-keys
        shift (if (xor (get ek SHIFT_KEY_NAME) (get ek CAPS_LOCK_NAME))
                SHIFT_OFFSET 0)
        ctrl (if (get ek CTRL_KEY_NAME) CTRL_OFFSET 0)
        alt  (if (get ek ALT_KEY_NAME) ALT_OFFSET 0)
        metak (if (get ek META_KEY_NAME) META_OFFSET 0)
        k (+ metak alt ctrl shift kc)
        key (get key-translation k)]
    (or key (str "Unknown-" k)))) ;; consider using key-orig

(defn default-key-fn [& args]
  (let [[key id e] args]
    ;; (println "DEFAULT KEY" key "ID" id) ;; DEBUG
    ))

(defn default-extra-fn [& args]
  (let [[key id e] args]
    ;; (println "EXTRA KEY" key "ID" id "⎇" (extra-keys-text)) ;; DEBUG
    ))

(defn keydown [e]
  (let [ek @extra-keys
        target (.-target e)
        id (keyword (.-id target))
        ;; key-orig (.-key e) ;; DEBUG
        kc (.-keyCode e)
        key (event->key kc)]
    ;; (println "KEYDOWN" key "=" key-orig kc id) ;; DEBUG
    (if (extra-keys-set key)
      (let [extra-id (keyword (str "extra" id))
            extra-fn (or (get ek extra-id)
                       (get ek :extra)
                       default-extra-fn)]
        (if (= key CAPS_LOCK_NAME)
          (swap! extra-keys update-in [CAPS_LOCK_NAME] not)
          (swap! extra-keys assoc key true))
        (extra-fn key id e))
       (let [key-fn (get ek id (get ek :default default-key-fn))]
        (key-fn key id e)))))

(defn keyup [e]
  (let [ek @extra-keys
        target (.-target e)
        id (keyword (.-id target))
        ;; key-orig (.-key e) ;; DEBUG
        kc (.-keyCode e)
        key (event->key kc)]
    ;; (println "KEYUP" key "=" key-orig kc id) ;; DEBUG
    (when (and (extra-keys-set key) (not= key CAPS_LOCK_NAME))
      (let [extra-id (keyword (str "extra" id))
            extra-fn (or (get ek extra-id)
                       (get ek :extra)
                       default-extra-fn)]
        (swap! extra-keys assoc key false)
        (extra-fn key id e)))
    ))
