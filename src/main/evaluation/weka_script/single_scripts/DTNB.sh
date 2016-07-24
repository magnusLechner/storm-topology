#DTNB
echo start DTNB

java -classpath ./weka.jar:$HOME/wekafiles/packages/DTNB/DTNB.jar weka.Run -no-load -no-scan weka.classifiers.rules.DTNB -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_3/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/DTNB_row3_1.txt
java -classpath ./weka.jar:$HOME/wekafiles/packages/DTNB/DTNB.jar weka.Run -no-load -no-scan weka.classifiers.rules.DTNB -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_4/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/DTNB_row4_1.txt
java -classpath ./weka.jar:$HOME/wekafiles/packages/DTNB/DTNB.jar weka.Run -no-load -no-scan weka.classifiers.rules.DTNB -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_5/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/DTNB_row5_1.txt
java -classpath ./weka.jar:$HOME/wekafiles/packages/DTNB/DTNB.jar weka.Run -no-load -no-scan weka.classifiers.rules.DTNB -o -t ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Training.arff -T ~/workspace/my-sentistorm/resources/arff/Twitch/row_6/Test.arff &> ~/workspace/my-sentistorm/evaluation/weka_script/script_results/DTNB_row6_1.txt

echo stop DTNB