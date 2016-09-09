#J48:
#   Confidence-factor: 0.05,0.15,0.25,0.3
echo start J48

java weka.classifiers.trees.J48 -C 0.05 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row3_1.txt
java weka.classifiers.trees.J48 -C 0.15 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row3_2.txt
java weka.classifiers.trees.J48 -C 0.25 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row3_3.txt
java weka.classifiers.trees.J48 -C 0.3 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row3_4.txt

java weka.classifiers.trees.J48 -C 0.05 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row4_1.txt
java weka.classifiers.trees.J48 -C 0.15 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row4_2.txt
java weka.classifiers.trees.J48 -C 0.25 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row4_3.txt
java weka.classifiers.trees.J48 -C 0.3 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row4_4.txt

java weka.classifiers.trees.J48 -C 0.05 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row5_1.txt
java weka.classifiers.trees.J48 -C 0.15 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row5_2.txt
java weka.classifiers.trees.J48 -C 0.25 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row5_3.txt
java weka.classifiers.trees.J48 -C 0.3 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row5_4.txt

java weka.classifiers.trees.J48 -C 0.05 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row6_1.txt
java weka.classifiers.trees.J48 -C 0.15 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row6_2.txt
java weka.classifiers.trees.J48 -C 0.25 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row6_3.txt
java weka.classifiers.trees.J48 -C 0.3 -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/J48_row6_4.txt

echo end J48